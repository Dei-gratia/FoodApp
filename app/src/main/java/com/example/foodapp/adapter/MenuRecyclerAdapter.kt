package com.example.foodapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.activity.MyCartActivity
import com.example.foodapp.model.MenuItems

class MenuRecyclerAdapter(val context: Context, val restaurantId: String, val restaurantName: String, val btnProceedToCart : Button, val menuList: List<MenuItems>): RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>(){

    var itemSelectedCount: Int = 0
    var count = 1
    var itemsSelectedId = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_row, parent, false)

        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val restaurantMenuItem = menuList[position]
        val btnAdd = holder.btnAdd

        holder.txtItemId.text = count.toString()
        holder.txtItemName.text = restaurantMenuItem.name
        holder.txtItemPrice.text = "Rs. ${restaurantMenuItem.cost_for_one}"
        count++

        btnProceedToCart.setOnClickListener {
            val intent = Intent(context, MyCartActivity::class.java)
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("restaurantName", restaurantName)
            intent.putExtra("selectedItemsId", itemsSelectedId)
            context.startActivity(intent)
        }

        holder.btnAdd.setOnClickListener {
            if (holder.btnAdd.text.toString() == "Remove") {
                itemSelectedCount--
                Toast.makeText(context, "${restaurantMenuItem.name} removed from cart", Toast.LENGTH_SHORT).show()
                btnAdd.text = "Add"
                itemsSelectedId.remove(restaurantMenuItem.id + "")
                val inCartColor =
                    ContextCompat.getColor(context.applicationContext, R.color.colorPrimary)
                btnAdd.setBackgroundColor(inCartColor)
            } else {
                itemSelectedCount++
                itemsSelectedId.add(restaurantMenuItem.id + "")
                Toast.makeText(context, "${restaurantMenuItem.name} added to cart", Toast.LENGTH_SHORT).show()
                btnAdd.text = "Remove"
                val notInCartColor =
                    ContextCompat.getColor(context.applicationContext, R.color.yellow)
                btnAdd.setBackgroundColor(notInCartColor)
            }

            if (itemSelectedCount > 0) {
                btnProceedToCart.visibility = View.VISIBLE
            } else {
                btnProceedToCart.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class MenuViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtItemName: TextView = view.findViewById(R.id.txtFoodName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val txtItemId: TextView = view.findViewById(R.id.txtFoodId)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    fun getSelectedItemCount(): Int {
        return itemSelectedCount
    }

}