package com.example.foodapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foodapp.R

class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtPhone: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = activity?.getSharedPreferences("User Preferences", Context.MODE_PRIVATE) as SharedPreferences
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName = view.findViewById(R.id.txtUserName)
        txtPhone = view.findViewById(R.id.txtPhone)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)

        txtName.text  = sharedPreferences.getString("Name", "name")
        txtEmail.text  = sharedPreferences.getString("Email", "email")
        txtPhone.text = sharedPreferences.getString("Phone", "phone")
        txtAddress.text  = sharedPreferences.getString("Address", "address")

        return view
    }


}
