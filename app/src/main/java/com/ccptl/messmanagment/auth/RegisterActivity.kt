package com.ccptl.messmanagment.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ccptl.messmanagment.MainActivity
import com.ccptl.messmanagment.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnAddRestaurant.setOnClickListener {
            startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
        }


    }
}