package com.example.rumiappadmin.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rumiappadmin.R
import com.example.rumiappadmin.databinding.ActivityMenuItemDetailsBinding
import com.example.rumiappadmin.firestore.FirestoreClass
import com.example.rumiappadmin.models.Menu
import com.example.rumiappadmin.utils.Constants
import com.example.rumiappadmin.utils.GlideLoader

class MenuItemDetailsActivity : BaseActivity() {

    private var _binding: ActivityMenuItemDetailsBinding? = null
    private val binding get() = _binding!!

    private var mMenuId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMenuItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_MENU_ID)) {
            mMenuId = intent.getStringExtra(Constants.EXTRA_MENU_ID)!!
            Log.i("Menu id", mMenuId)
        }
        setupActionBar()

        getMenuDetails()
    }

    fun getMenuDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMenuDetails(this@MenuItemDetailsActivity, mMenuId)
    }

    fun menuDetailsSuccess(menu: Menu) {
        hideProgressDialog()

        binding.apply {
            GlideLoader(this@MenuItemDetailsActivity).loadMenuItemPicture(menu.image, ivMenuDetailImage)
            tvMenuDetailsTitle.text = menu.title
            tvMenuDetailsPrice.text = menu.price
            tvMenuDetailsDescription.text = menu.description
        }
    }

    fun setupActionBar() {
        val toolbar = binding.toolbarMenuDetailsActivity
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}