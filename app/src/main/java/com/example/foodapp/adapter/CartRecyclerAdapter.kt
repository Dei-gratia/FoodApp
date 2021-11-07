package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.model.MenuItems

class CartRecyclerAdapter(val context: Context, private val cartList: ArrayList<MenuItems>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_row, parent, false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val food = cartList[position]
        holder.txtFoodName.text = food.name
        holder.txtFoodPrice.text = food.cost_for_one

    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    class CartViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtFoodName: TextView = view.findViewById(R.id.txtOrderItem)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtOrderItemPrice)
    }

}