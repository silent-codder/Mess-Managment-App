package com.ccptl.messmanagment.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ccptl.messmanagment.BuildConfig
import com.ccptl.messmanagment.MainActivity
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.utils.Constants.Companion.FIREBASE_LOGIN_DATA
import com.ccptl.messmanagment.utils.Constants.Companion.FIREBASE_LOGIN_DATE
import com.ccptl.messmanagment.utils.Constants.Companion.FIREBASE_LOGIN_EMAIL
import com.ccptl.messmanagment.utils.Constants.Companion.FIREBASE_LOGIN_PASSWORD
import com.ccptl.messmanagment.utils.Constants.Companion.FIREBASE_USER_ID
import com.ccptl.messmanagment.utils.Constants.Companion.FIREBASE_USER_TYPE
import com.ccptl.messmanagment.utils.Constants.Companion.FIREBASE_VERSION_NAME
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var pbLoader: ProgressBar
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        pbLoader = findViewById(R.id.pbLoader)
        btnLogin = findViewById(R.id.btnLogin)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
//        tvVersion.text = "Version : ${BuildConfig.VERSION_NAME}"
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {

            val email = etEmail.text.trim().toString()
            val password = etPassword.text.trim().toString()

            if (email.isEmpty()) {
                tilEmail.error = "Empty field"
            } else if (!EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Invalid Email"
            } else if (TextUtils.isEmpty(password)) {
                tilEmail.error = null
                tilPassword.error = "Invalid Password"
            } else {
                tilEmail.error = null
                tilPassword.error = null
                login(email, password)
            }
        }

    }

    private fun login(email: String, password: String) {
        btnLogin.visibility = View.GONE
        pbLoader.visibility = View.VISIBLE

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            if (it.isSuccessful) {

                var loginHashMap = HashMap<String, String>()
                loginHashMap[FIREBASE_LOGIN_EMAIL] = email
                loginHashMap[FIREBASE_LOGIN_PASSWORD] = password
                loginHashMap[FIREBASE_LOGIN_DATE] = Calendar.getInstance().time.toString()
                loginHashMap[FIREBASE_USER_TYPE] = ""
                loginHashMap[FIREBASE_VERSION_NAME] = BuildConfig.VERSION_NAME
                loginHashMap[FIREBASE_USER_ID] = firebaseAuth.currentUser?.uid.toString()

                var currentUserId = firebaseAuth.currentUser?.uid.toString()

                firebaseFirestore.collection(FIREBASE_LOGIN_DATA)
                    .document(currentUserId).set(loginHashMap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }.addOnFailureListener {
                        pbLoader.visibility = View.GONE
                        btnLogin.visibility = View.VISIBLE
                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT)
                            .show()
                    }

            }
        }.addOnFailureListener {
            Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
            pbLoader.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
        }

    }

}