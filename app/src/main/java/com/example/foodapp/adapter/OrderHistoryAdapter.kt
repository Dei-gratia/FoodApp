package com.example.foodapp.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.model.MenuItems
import com.example.foodapp.model.OrderHistoryRestaurant
import com.example.foodapp.util.ConnectionManager
import org.json.JSONException

class OrderHistoryAdapter(val context: Context, private val orderedRestaurantList: ArrayList<OrderHistoryRestaurant>
) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderOrderHistoryRestaurant>() {

    lateinit var sharedPreferences: SharedPreferences

    class ViewHolderOrderHistoryRestaurant(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerViewItemsOrdered: RecyclerView = view.findViewById(R.id.recyclerViewItemsOrdered)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderOrderHistoryRestaurant {
        sharedPreferences = context.getSharedPreferences("User Preferences", Context.MODE_PRIVATE)

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_recycler_view_single_row, parent, false)

        return ViewHolderOrderHistoryRestaurant(view)
    }

    override fun getItemCount(): Int {
        return orderedRestaurantList.size
    }

    override fun onBindViewHolder(holder: ViewHolderOrderHistoryRestaurant, position: Int) {

        val restaurantObject = orderedRestaurantList[position]
        holder.txtRestaurantName.text = restaurantObject.restaurantName
        var formatDate = restaurantObject.orderPlacedAt
        formatDate = formatDate.replace("-", "/")
        formatDate = formatDate.substring(0, 6) + "20" + formatDate.substring(6, 8)
        holder.txtDate.text = formatDate

        val layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: CartRecyclerAdapter

        if (ConnectionManager().isNetworkAvailable(context)) {

            try {
                val orderItemsPerRestaurant = ArrayList<MenuItems>()
                val userId = sharedPreferences.getString("UserID", "1")
                val queue = Volley.newRequestQueue(context)
                val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {

                        println(it)
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")
                        if (success) {

                            val data = response.getJSONArray("data")
                            val restaurantObjectRetrieved = data.getJSONObject(position)
                            orderItemsPerRestaurant.clear()
                            val foodOrdered = restaurantObjectRetrieved.getJSONArray("food_items")

                            for (j in 0 until foodOrdered.length())
                            {
                                val eachFoodItem = foodOrdered.getJSONObject(j)
                                val itemObject = MenuItems(
                                    eachFoodItem.getString("food_item_id"),
                                    eachFoodItem.getString("name"),
                                    eachFoodItem.getString("cost"), "000"
                                )

                                orderItemsPerRestaurant.add(itemObject)
                            }

                            orderedItemAdapter = CartRecyclerAdapter(context, orderItemsPerRestaurant)
                            holder.recyclerViewItemsOrdered.adapter = orderedItemAdapter
                            holder.recyclerViewItemsOrdered.layoutManager = layoutManager
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "402f2a04936da5"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    context,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}