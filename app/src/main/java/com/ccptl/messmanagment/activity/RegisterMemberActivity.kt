package com.ccptl.messmanagment.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ccptl.messmanagment.BuildConfig
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.utils.Constants
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class RegisterMemberActivity : AppCompatActivity() {

    private lateinit var ivBtnBack: ImageView
    private lateinit var version: TextView
    private lateinit var tilName: TextInputLayout
    private lateinit var tilMobile: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etMobile: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnRegisterMember: Button
    private lateinit var pbLoader: ProgressBar

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dataBase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val CURRENT_USER_ID = firebaseAuth.currentUser?.uid

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_member)

        ivBtnBack = findViewById(R.id.ivBtnBack)
        version = findViewById(R.id.version)
        tilName = findViewById(R.id.tilName)
        tilMobile = findViewById(R.id.tilMobile)
        tilEmail = findViewById(R.id.tilEmail)
        etName = findViewById(R.id.etName)
        etMobile = findViewById(R.id.etMobile)
        etEmail = findViewById(R.id.etEmail)
        btnRegisterMember = findViewById(R.id.btnRegisterMember)
        pbLoader = findViewById(R.id.pbLoader)

        ivBtnBack.setOnClickListener { onBackPressed() }
        version.text = "v" + BuildConfig.VERSION_NAME

        btnRegisterMember.setOnClickListener {
            if (etName.text.toString().isEmpty()) {
                tilName.error = "Please enter name"
            } else if (etMobile.text.toString().isEmpty()) {
                tilMobile.error = "Please enter mobile number"
            } else if (etEmail.text.toString().isEmpty()) {
                tilEmail.error = "Please enter email"
            } else {
                tilName.error = null
                tilMobile.error = null
                tilEmail.error = null
                addEntry(etName.text.toString(), etMobile.text.toString(), etEmail.text.toString())
            }
        }

    }

    private fun addEntry(name: String, mobile: String, email: String) {
        pbLoader.visibility = ProgressBar.VISIBLE
        btnRegisterMember.isEnabled = false

        val memberId: String = dataBase.reference.child("members").push().key!!
        val member = hashMapOf(
            "memberId" to memberId,
            "name" to name,
            "mobile" to mobile,
            "email" to email,
            "createdBy" to CURRENT_USER_ID,
            "createdOn" to System.currentTimeMillis(),
            "updatedBy" to CURRENT_USER_ID,
            "updatedOn" to System.currentTimeMillis(),
            "isDeleted" to false,
            "deletedBy" to "",
        )

        fireStore.collection("members")
            .document(memberId)
            .set(member)
            .addOnSuccessListener {
                pbLoader.visibility = ProgressBar.GONE
                btnRegisterMember.isEnabled = true

                val lastEntry = hashMapOf(
                    "lastEntryMemberData" to true,
                    "lastEntryMessData" to false,
                )

                fireStore.collection("lastEntry")
                    .document(CURRENT_USER_ID!!)
                    .set(lastEntry).addOnCompleteListener {
                        Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show()
                        etName.setText("")
                        etMobile.setText("")
                        etEmail.setText("")
                        onBackPressed()
                    }

            }
            .addOnFailureListener {
                pbLoader.visibility = ProgressBar.GONE
                btnRegisterMember.isEnabled = true
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }
}