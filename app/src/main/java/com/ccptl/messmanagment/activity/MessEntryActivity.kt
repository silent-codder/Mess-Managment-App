package com.ccptl.messmanagment.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ccptl.messmanagment.BuildConfig
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.utils.Constants
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_member.*
import java.text.SimpleDateFormat
import java.util.*

class MessEntryActivity : AppCompatActivity() {

    private lateinit var ivBtnBack: ImageView
    private lateinit var version: TextView
    private lateinit var tilDate: TextInputLayout
    private lateinit var tilPayment: TextInputLayout
    private lateinit var etPayment: EditText
    private lateinit var etDate: EditText
    private lateinit var rgMessType: RadioGroup
    private lateinit var rgPaymentType: RadioGroup
    private lateinit var btnMessEntry: Button
    private lateinit var pbLoader: ProgressBar

    private var messType: String = ""
    private var paymentType: String = ""
    private var fromDate: String = ""
    private var toDate: String = ""
    private var fromDateTimeStamp: String = ""
    private var toDateTimeStamp: String = ""
    private var MEMBER_ID: String = ""

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dataBase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val CURRENT_USER_ID = firebaseAuth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mess_entry)

        ivBtnBack = findViewById(R.id.ivBtnBack)
        version = findViewById(R.id.version)
        tilDate = findViewById(R.id.tilDate)
        tilPayment = findViewById(R.id.tilPayment)
        etDate = findViewById(R.id.etDate)
        etPayment = findViewById(R.id.etPayment)
        rgMessType = findViewById(R.id.rgMessType)
        rgPaymentType = findViewById(R.id.rgPaymentType)
        btnMessEntry = findViewById(R.id.btnMessEntry)
        pbLoader = findViewById(R.id.pbLoader)

        ivBtnBack.setOnClickListener { onBackPressed() }
        version.text = "v" + BuildConfig.VERSION_NAME

        val memberId = intent.hasExtra("memberId")
        if (memberId != null) {
            MEMBER_ID = intent.getStringExtra("memberId").toString()
        }

        rgMessType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbDayNight -> {
                    messType = "DayNight"
                }
                R.id.rbDay -> {
                    messType = "Day"
                }
                R.id.rbNight -> {
                    messType = "Night"
                }
            }
        }

        rgPaymentType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCash -> {
                    paymentType = "Cash"
                }
                R.id.rbOnline -> {
                    paymentType = "Online"
                }
                R.id.rbPending -> {
                    paymentType = "Pending"
                }
            }
        }

        tilDate.setEndIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder
                .dateRangePicker()
                .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                .build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                fromDate = getDate(it.first).toString()
                fromDateTimeStamp = it.first.toString()
                toDate = getDate(it.second).toString()
                toDateTimeStamp = it.second.toString()
                etDate.setText("$fromDate - $toDate")
            }
            datePicker.addOnNegativeButtonClickListener {
                etDate.setText("")
            }
            datePicker.addOnCancelListener {
                etDate.setText("")
            }
        }

        btnMessEntry.setOnClickListener {
            if (etDate.text.toString().isEmpty()) {
                tilDate.error = "Please select date"
            } else if (etPayment.text.toString().isEmpty()) {
                tilPayment.error = "Please enter payment"
            } else if (messType.isEmpty()) {
                Toast.makeText(this, "Please select mess type", Toast.LENGTH_SHORT).show()
            } else if (paymentType.isEmpty()) {
                Toast.makeText(this, "Please select payment type", Toast.LENGTH_SHORT).show()
            } else {
                tilDate.error = null
                tilPayment.error = null
                pbLoader.visibility = ProgressBar.VISIBLE
                val messId: String = dataBase.reference.child("members").push().key!!
                val messEntry = hashMapOf(
                    "memberId" to MEMBER_ID,
                    "messId" to messId,
                    "fromDate" to fromDate,
                    "fromDateTimeStamp" to fromDateTimeStamp,
                    "toDate" to toDate,
                    "toDateTimeStamp" to toDateTimeStamp,
                    "payment" to etPayment.text.toString(),
                    "messType" to messType,
                    "paymentType" to paymentType,
                    "createdBy" to CURRENT_USER_ID,
                    "createdOn" to System.currentTimeMillis(),
                    "updatedBy" to CURRENT_USER_ID,
                    "updatedOn" to System.currentTimeMillis(),
                    "status" to Constants.ACTIVE,
                    "isDeleted" to false,
                    "deletedBy" to "",
                )
                fireStore.collection("messEntry")
                    .add(messEntry)
                    .addOnSuccessListener {
                        pbLoader.visibility = ProgressBar.GONE

                        val lastEntry = hashMapOf(
                            "lastEntryMemberData" to false,
                            "lastEntryMessData" to true,
                        )

                        fireStore.collection("lastEntry")
                            .document(CURRENT_USER_ID!!)
                            .set(lastEntry).addOnCompleteListener {
                                Toast.makeText(this, "Mess entry added successfully", Toast.LENGTH_SHORT)
                                    .show()
                                etDate.setText("")
                                etPayment.setText("")
                                rgMessType.clearCheck()
                                rgPaymentType.clearCheck()
                                onBackPressed()
                            }

                    }
                    .addOnFailureListener {
                        pbLoader.visibility = ProgressBar.GONE
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(milliSeconds: Long): String? {
        val formatter = SimpleDateFormat("dd MMM")
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds.toLong()
        return formatter.format(calendar.time)
    }

}