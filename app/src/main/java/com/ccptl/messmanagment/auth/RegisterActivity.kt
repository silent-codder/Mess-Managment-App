package com.ccptl.messmanagment.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ccptl.messmanagment.BuildConfig
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.activity.MemberListActivity
import com.ccptl.messmanagment.utils.Constants
import com.ccptl.messmanagment.utils.PrefHelper
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var tilResName: TextInputLayout
    private lateinit var tilOwnerName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilMobile: TextInputLayout
    private lateinit var tilWebsite: TextInputLayout
    private lateinit var etResName: EditText
    private lateinit var etOwnerName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etWebsite: EditText
    private lateinit var rgResType: RadioGroup
    private lateinit var btnAddRestaurant: Button
    private lateinit var version: TextView
    private lateinit var resLoader: ProgressBar
    private var CURRENT_USER_ID: String = ""

    private lateinit var prefHelper: PrefHelper
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dataBase: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tilResName = findViewById(R.id.tilResName)
        tilOwnerName = findViewById(R.id.tilResOwnerName)
        tilEmail = findViewById(R.id.tilEmail)
        tilMobile = findViewById(R.id.tilResMobile)
        tilWebsite = findViewById(R.id.tilWebsite)
        etResName = findViewById(R.id.etResName)
        etOwnerName = findViewById(R.id.etResOwnerName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etResMobile)
        etWebsite = findViewById(R.id.etWebsite)
        rgResType = findViewById(R.id.rgResType)
        btnAddRestaurant = findViewById(R.id.btnAddRestaurant)
        version = findViewById(R.id.version)
        resLoader = findViewById(R.id.resLoader)


        version.text = "v" + BuildConfig.VERSION_NAME
        prefHelper = PrefHelper(this)

        btnAddRestaurant.setOnClickListener {
            val resName = etResName.text.toString()
            val ownerName = etOwnerName.text.toString()
            val email = etEmail.text.toString()
            val mobile = etMobile.text.toString()
            val website = etWebsite.text.toString()
            val resTypeId = rgResType.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(resTypeId)
            val resType = radioButton.text.toString()

            if (resName.isEmpty()) {
                tilResName.error = "Enter Restaurant Name"
                return@setOnClickListener
            } else {
                tilResName.error = null
            }

            if (ownerName.isEmpty()) {
                tilOwnerName.error = "Enter Owner Name"
                return@setOnClickListener
            } else {
                tilOwnerName.error = null
            }

            if (email.isEmpty()) {
                tilEmail.error = "Enter Email"
                return@setOnClickListener
            } else {
                tilEmail.error = null
            }

            if (mobile.isEmpty()) {
                tilMobile.error = "Enter Mobile Number"
                return@setOnClickListener
            } else {
                tilMobile.error = null
            }

            if (resTypeId == -1) {
                Toast.makeText(this, "Select Restaurant Type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resLoader.visibility = ProgressBar.VISIBLE
            btnAddRestaurant.visibility = Button.GONE

            firebaseAuth.createUserWithEmailAndPassword(email, "123456")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        CURRENT_USER_ID = firebaseAuth.currentUser?.uid.toString()
                        val restaurant = hashMapOf(
                            "resKey" to CURRENT_USER_ID,
                            "resName" to resName,
                            "ownerName" to ownerName,
                            "email" to email,
                            "mobile" to mobile,
                            "website" to website,
                            "resType" to resType,
                            "createdOn" to System.currentTimeMillis(),
                            "updatedBy" to "",
                            "updatedOn" to System.currentTimeMillis(),
                            "status" to Constants.ACTIVE,
                            "isDeleted" to false,
                            "deletedBy" to "",
                        )
                        fireStore.collection("restaurants")
                            .document(CURRENT_USER_ID!!)
                            .set(restaurant)
                            .addOnSuccessListener {
                                prefHelper.put(Constants.CHECK_LAST_ENTRY_MEMBER_DATA, true)
                                prefHelper.put(Constants.CHECK_LAST_ENTRY_MESS_HISTORY_DATA, true)
                                resLoader.visibility = ProgressBar.GONE
                                btnAddRestaurant.visibility = Button.VISIBLE
                                Toast.makeText(
                                    this,
                                    "Restaurant Added Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, MemberListActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT)
                                    .show()
                                resLoader.visibility = ProgressBar.GONE
                                btnAddRestaurant.visibility = Button.VISIBLE
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        resLoader.visibility = ProgressBar.GONE
                        btnAddRestaurant.visibility = Button.VISIBLE
                    }
                }

        }

    }
}