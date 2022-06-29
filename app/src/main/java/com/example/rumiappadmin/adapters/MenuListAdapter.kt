package com.example.rumiappadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rumiappadmin.databinding.ItemMenuListBinding
import com.example.rumiappadmin.models.Menu
import com.example.rumiappadmin.ui.fragments.MenuFragment
import com.example.rumiappadmin.utils.GlideLoader

class MenuListAdapter (
    private val context: Context,
    private val list: ArrayList<Menu>,
    private val fragment: MenuFragment
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class MenuListViewHolder(private val binding: ItemMenuListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, menu: Menu, fragment: MenuFragment) {
            binding.apply {
                GlideLoader(context).loadMenuItemPicture(menu.image, ivAvatar)
                tvMenuName.text = menu.title
                tvMenuPrice.text = "Rp${menu.price},-"

                ibDeleteProduct.setOnClickListener {
                    fragment.deleteMenu(menu.menu_id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMenuListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MenuListViewHolder) {
            holder.bind(context, list[position], fragment)
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, list[position])
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, menu: Menu)
    }
}