package com.example.foodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var btnRegister: Button
    lateinit var etName: EditText
    lateinit var etPhoneNumber: EditText
    lateinit var etPassword: EditText
    lateinit var etEmail: EditText
    lateinit var etAddress: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var rlRegister: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("User Preferences", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_register)

        init()

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnRegister.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            val intent = Intent(this, HomeActivity::class.java)

            val name = etName.text.toString()
            val phone = etPhoneNumber.text.toString()
            val email = etEmail.text.toString()
            val address = etAddress.text.toString()
            val password = etPassword.text.toString()

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/register/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("name", name)
            jsonParams.put("mobile_number", phone)
            jsonParams.put("password", password)
            jsonParams.put("address", address)
            jsonParams.put("email", email)

            if (ConnectionManager().isNetworkAvailable(this)) {

                val jsonRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            println("Response register is $it")
                            println("Response register is $success")

                            if (success) {
                                val userJsonObject = data.getJSONObject("data")
                                progressLayout.visibility = View.GONE

                                val userId = userJsonObject.getString("user_id")
                                val userName = userJsonObject.getString("name")
                                val userEmail = userJsonObject.getString("email")
                                val userPhone = userJsonObject.getString("mobile_number")
                                val userAddress = userJsonObject.getString("address")

                                sharedPreferences.edit().putBoolean("LoggedIn", true).apply()
                                sharedPreferences.edit().putBoolean("Registered", true).apply()
                                sharedPreferences.edit().putString("UserId", userId).apply()
                                sharedPreferences.edit().putString("Name", userName).apply()
                                sharedPreferences.edit().putString("Phone", userPhone).apply()
                                sharedPreferences.edit().putString("Email", userEmail).apply()
                                sharedPreferences.edit().putString("Address", userAddress).apply()
                                startActivity(intent)
                                finish()

                            } else {
                                progressLayout.visibility = View.GONE
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        rlRegister = findViewById(R.id.rlRegister)
        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
    }
}
