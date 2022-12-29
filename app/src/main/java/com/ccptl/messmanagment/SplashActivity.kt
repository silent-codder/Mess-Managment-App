package com.ccptl.messmanagment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ccptl.messmanagment.auth.RegisterActivity
import com.ccptl.messmanagment.room.DemoData
import com.ccptl.messmanagment.room.DemoDatabase
import com.ccptl.messmanagment.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Timer

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val background = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(3000)
                    val i = Intent(baseContext, MainActivity::class.java)
                    startActivity(i)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }
}