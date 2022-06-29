package com.example.rumiappadmin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rumiappadmin.databinding.ItemCartListBinding
import com.example.rumiappadmin.models.Cart
import com.example.rumiappadmin.ui.activities.OrderDetailsActivity
import com.example.rumiappadmin.utils.Constants
import com.example.rumiappadmin.utils.GlideLoader

class CartItemListAdapter(
    private val context: Context,
    private val list: ArrayList<Cart>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class CartItemListViewHolder(private val binding: ItemCartListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, cart: Cart) {
            binding.apply {
                GlideLoader(context).loadUserPicture(cart.image, ivCartItemImage)
                tvCartItemTitle.text = cart.title
                tvCartItemPrice.text = "Rp${cart.price},-"
                tvCartQuantity.text = cart.cart_quantity
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCartListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CartItemListViewHolder) {
            holder.bind(context, list[position])
        }
    }

    override fun getItemCount(): Int = list.size
}