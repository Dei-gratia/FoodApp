package com.example.foodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.adapter.AllRestaurantsAdapter
import com.example.foodapp.adapter.MenuRecyclerAdapter
import com.example.foodapp.database.RestaurantDatabase
import com.example.foodapp.database.RestaurantEntity
import com.example.foodapp.util.ConnectionManager
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var menuRecyclerAdapter: MenuRecyclerAdapter
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var btnCart: Button
    lateinit var toolBar: Toolbar
    lateinit var imgIsFav: ImageView


    var restaurantID: Int? = 0
    var restaurantName: String? = "0"
    var restaurantRating: String? = "0"
    var restaurantImg: String? = "0"
    var restaurantCostForTwo: String? = "0"

    val menuList = ArrayList<com.example.foodapp.model.MenuItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        if (intent != null) {
            restaurantID = intent.getIntExtra("Restaurant_id", 0)
            restaurantName = intent.getStringExtra("Restaurant_name")
            restaurantRating = intent.getStringExtra("Restaurant_rating")
            restaurantCostForTwo = intent.getStringExtra("Restaurant_costForTwo")
            restaurantImg = intent.getStringExtra("Restaurant_img")
        }
        if (restaurantID == 0) {
            finish()
            Toast.makeText(this, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }

        init()
        setToolBar()

        val restaurantEntity = RestaurantEntity(
            restaurantID!!,
            restaurantName.toString(),
            restaurantRating.toString(),
            restaurantCostForTwo.toString(),
            restaurantImg.toString()
        )

        val isFavourate = FavouriteAsync(this, restaurantEntity, 1).execute().get()
        if (isFavourate){
            imgIsFav.setImageResource(R.drawable.ic_action_fav_checked)
        } else{
            imgIsFav.setImageResource(R.drawable.ic_action_fav)
        }

        imgIsFav.setOnClickListener {

            if (!isFavourate) {
                val async = AllRestaurantsAdapter.DBAsyncTask(this, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    imgIsFav.setImageResource(R.drawable.ic_action_fav_checked)
                    Toast.makeText(this, "Added to Favourites", Toast.LENGTH_LONG).show()
                    isFavourate == true
                }
            } else {
                val async = AllRestaurantsAdapter.DBAsyncTask(this, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    imgIsFav.setImageResource(R.drawable.ic_action_fav)
                    Toast.makeText(this, "Removed from Favourites", Toast.LENGTH_LONG).show()
                    isFavourate == false
                }
            }

        }

        layoutManager = LinearLayoutManager(this)

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${restaurantID}"


        if (ConnectionManager().isNetworkAvailable(this)) {

            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    val response = it.getJSONObject("data")
                    try {
                        progressLayout.visibility = View.GONE
                        val success = response.getBoolean("success")
                        if (success) {
                            val data = response.getJSONArray("data")
                            progressLayout.visibility = View.GONE


                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = com.example.foodapp.model.MenuItems(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost_for_one"),
                                    foodJsonObject.getString("restaurant_id")
                                )
                                menuList.add(foodObject)

                                menuRecyclerAdapter = MenuRecyclerAdapter(
                                    this,
                                    restaurantID.toString(),
                                    restaurantName.toString(),
                                    btnCart,
                                    menuList
                                )
                                recyclerMenu.adapter = menuRecyclerAdapter
                                recyclerMenu.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(this, "Some Error Occurred!!!", Toast.LENGTH_SHORT)
                                .show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(this, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {

                    Toast.makeText(this, "Volley error occurred!!!", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "402f2a04936da5"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun init(){
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.GONE
        recyclerMenu = findViewById(R.id.recyclerMenu)
        btnCart = findViewById(R.id.btnCart)
        toolBar = findViewById(R.id.toolBar)
        imgIsFav = findViewById(R.id.imgIsFav)
    }

    private fun setToolBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }

    override fun onBackPressed() {
        back()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                back()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun back() {
        if (menuRecyclerAdapter.getSelectedItemCount() > 0) {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will Empty the cart")
            alterDialog.setPositiveButton("Okay") { _, _ ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("Cancel") { _, _ ->

            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}

class FavouriteAsync(context: Context, private val restaurantEntity: RestaurantEntity, private val mode: Int) : AsyncTask<Void, Void, Boolean>() {

    private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {
        when (mode) {

            1 -> {
                val res: RestaurantEntity? =
                    db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                db.close()
                return res != null
            }

            2 -> {
                db.restaurantDao().insertRestaurant(restaurantEntity)
                db.close()
                return true
            }

            3 -> {
                db.restaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                return true
            }
        }

        return false
    }

}
