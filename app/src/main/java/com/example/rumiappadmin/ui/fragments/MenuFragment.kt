package com.example.rumiappadmin.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rumiappadmin.R
import com.example.rumiappadmin.adapters.MenuListAdapter
import com.example.rumiappadmin.databinding.FragmentMenuBinding
import com.example.rumiappadmin.firestore.FirestoreClass
import com.example.rumiappadmin.ui.activities.AddMenuItemActivity
import com.example.rumiappadmin.ui.activities.MenuItemDetailsActivity
import com.example.rumiappadmin.utils.Constants

class MenuFragment : BaseFragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMenuListFirestore()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_menu -> {
                startActivity(Intent(activity, AddMenuItemActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getMenuListFirestore() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMenuList(this@MenuFragment)
    }

    fun successGetMenuList(menuList: ArrayList<com.example.rumiappadmin.models.Menu>) {
        hideProgressDialog()

        for (i in menuList) {
            Log.i("item title: ", i.title)
        }

        binding.apply {
            if (menuList.size > 0) {
                rvMenuItems.visibility = View.VISIBLE
                tvNoMenuItemsFound.visibility = View.GONE

                rvMenuItems.layoutManager = LinearLayoutManager(activity)
                rvMenuItems.setHasFixedSize(true)

                val adapter = MenuListAdapter(requireContext(), menuList, this@MenuFragment)
                rvMenuItems.adapter = adapter

                adapter.setOnClickListener(object : MenuListAdapter.OnClickListener {
                    override fun onClick(
                        position: Int,
                        menu: com.example.rumiappadmin.models.Menu
                    ) {
                        val intent = Intent(context, MenuItemDetailsActivity::class.java)
                        intent.putExtra(Constants.EXTRA_MENU_ID, menu.menu_id)
                        startActivity(intent)
                    }

                })
            } else {
                rvMenuItems.visibility = View.GONE
                tvNoMenuItemsFound.visibility = View.VISIBLE
            }
        }
    }

    fun deleteMenu(menuId: String) {

        showDeleteMenuDialog(menuId)
    }
    fun successDeleteMenuItem() {
        hideProgressDialog()

        getMenuListFirestore()
    }

    fun showDeleteMenuDialog(menuId: String) {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().deleteMenu(this@MenuFragment, menuId)

            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}