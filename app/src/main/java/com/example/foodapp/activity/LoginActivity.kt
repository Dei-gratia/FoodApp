package com.example.foodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var registerYourself: TextView
    private lateinit var login: Button
    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var txtForgotPassword: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("User Preferences", Context.MODE_PRIVATE)
        val loggedIn = sharedPreferences.getBoolean("LoggedIn", false)
        setContentView(R.layout.activity_login)

        if(loggedIn){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        init()

        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        registerYourself.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


        login.setOnClickListener {

            val phone = etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", phone)
            jsonParams.put("password", password)

            if (ConnectionManager().isNetworkAvailable(this)) {

                val jsonRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val userJsonObject = data.getJSONObject("data")

                                val userId = userJsonObject.getString("user_id")
                                val userName = userJsonObject.getString("name")
                                val userEmail = userJsonObject.getString("email")
                                val userPhone = userJsonObject.getString("mobile_number")
                                val userAddress = userJsonObject.getString("address")

                                sharedPreferences.edit().putBoolean("LoggedIn", true).apply()
                                sharedPreferences.edit().putString("UserId", userId).apply()
                                sharedPreferences.edit().putString("Name", userName).apply()
                                sharedPreferences.edit().putString("Phone", userPhone).apply()
                                sharedPreferences.edit().putString("Email", userEmail).apply()
                                sharedPreferences.edit().putString("Address", userAddress).apply()
                                startActivity(intent)
                                finish()

                            } else {
                                val errorMassage = data.getString("errorMessage")
                                Toast.makeText(this, errorMassage, Toast.LENGTH_LONG)
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
    }

    private fun init() {
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        registerYourself = findViewById(R.id.txtRegisterYourself)
        login = findViewById(R.id.btnLogin)
    }
}
