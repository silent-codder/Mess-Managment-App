package com.ccptl.messmanagment.member

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ccptl.messmanagment.BuildConfig
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.utils.Constants
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_member.*
import java.text.SimpleDateFormat
import java.util.*

class AddMemberActivity : AppCompatActivity() {

    private var messType: String = ""
    private var paymentMode: String = ""
    private var fromDate: String = ""
    private var toDate: String = ""
    private var fromDateTimeStamp: String = ""
    private var toDateTimeStamp: String = ""

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private val dataBase: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        version.text = "V-${BuildConfig.VERSION_NAME}"

        ivBtnBack.setOnClickListener {
            onBackPressed()
        }

        rbDayNight.setOnClickListener {
            messType = "Day-Night"
        }
        rbDay.setOnClickListener {
            messType = "Day"
        }
        rbNight.setOnClickListener {
            messType = "Night"
        }
        rbCash.setOnClickListener {
            paymentMode = "Cash"
        }
        rbOnline.setOnClickListener {
            paymentMode = "Online"
        }
        rbPending.setOnClickListener {
            paymentMode = "Pending"
        }

        tilDate.setEndIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder
                .dateRangePicker()
                .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                .build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
//                Log.d(TAG, "onCreate: ${getDate(it.first)} | ${getDate(it.second)}")
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

        btnAddMember.setOnClickListener {

            val name = etName.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val date = etDate.text.toString().trim()
            val payment = etPayment.text.toString().trim()

            if (TextUtils.isEmpty(name)) {
                tilName.error = "Full Name required"
            } else {
                tilName.error = null
            }

            if (TextUtils.isEmpty(mobile)) {
                tilMobile.error = "Mobile required"
            } else {
                tilMobile.error = null
            }

            if (TextUtils.isEmpty(date)) {
                tilDate.error = "Date required"
            } else {
                tilDate.error = null
            }

            if (TextUtils.isEmpty(payment)) {
                tilPayment.error = "Payment amount required"
            } else {
                tilPayment.error = null
            }

            if (TextUtils.isEmpty(messType)) {
                Toast.makeText(this@AddMemberActivity, "Mess type choose", Toast.LENGTH_SHORT)
                    .show()
            } else if (TextUtils.isEmpty(paymentMode)) {
                Toast.makeText(this@AddMemberActivity, "Payment method choose", Toast.LENGTH_SHORT)
                    .show()
            } else {

                loader.visibility = View.VISIBLE
                btnAddMember.visibility = View.GONE

                val key: String =
                    dataBase.getReference(Constants.FIREBASE_MESS_MEMBER_DATA).push().key.toString()
                val map = HashMap<String, String>()
                map[Constants.FIREBASE_MESS_MEMBER_NAME] = name
                map[Constants.FIREBASE_MESS_MEMBER_ID] = key
                map[Constants.FIREBASE_MESS_MEMBER_MOBILE] = mobile
                map[Constants.FIREBASE_MESS_TYPE] = messType
                map[Constants.FIREBASE_MESS_FROM_DATE] = fromDateTimeStamp
                map[Constants.FIREBASE_MESS_TO_DATE] = toDateTimeStamp
                map[Constants.FIREBASE_MESS_PAYMENT] = payment
                map[Constants.FIREBASE_MESS_PAYMENT_MODE] = paymentMode
                map[Constants.RESTAURANT_ID] = firebaseAuth.currentUser?.uid.toString()
                map[Constants.FIREBASE_MESS_CREATED_AT] = Calendar.getInstance().time.toString()
                map[Constants.FIREBASE_MESS_UPDATED_AT] = Calendar.getInstance().time.toString()
                map[Constants.FIREBASE_MESS_CLOSE_AT] = ""
                map[Constants.FIREBASE_MESS_DELETE_AT] = ""

                val entryMap = HashMap<String, Boolean>()
                entryMap[Constants.CHECK_LAST_ENTRY_MEMBER_DATA] = true
                entryMap[Constants.CHECK_LAST_ENTRY_MESS_HISTORY_DATA] = true

                firestore.collection(Constants.FIREBASE_CHECK_LAST_ENTRY_DATA).document(firebaseAuth.currentUser?.uid.toString())
                    .set(entryMap)

                firestore.collection(Constants.FIREBASE_MESS_MEMBER_DATA)
                    .document(key).set(map).addOnCompleteListener {
                        if (it.isSuccessful) {
                            loader.visibility = View.GONE
                            btnAddMember.visibility = View.VISIBLE
                            Toast.makeText(this, "Entry successfully", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        }
                    }.addOnFailureListener {
                        loader.visibility = View.GONE
                        btnAddMember.visibility = View.VISIBLE
                        Toast.makeText(this, "Failed to add member", Toast.LENGTH_SHORT).show()
                    }


                val historyKey: String =
                    dataBase.getReference(Constants.FIREBASE_MESS_HISTORY_DATA).push().key.toString()

                val historyMap = HashMap<String, String>()
                historyMap[Constants.FIREBASE_MESS_HISTORY_ID] = historyKey
                historyMap[Constants.FIREBASE_MESS_HISTORY_MEMBER_ID] = key
                historyMap[Constants.FIREBASE_MESS_FROM_DATE] = fromDateTimeStamp
                historyMap[Constants.FIREBASE_MESS_TO_DATE] = toDateTimeStamp
                historyMap[Constants.RESTAURANT_ID] = firebaseAuth.currentUser?.uid.toString()
                historyMap[Constants.FIREBASE_MESS_CREATED_AT] = Calendar.getInstance().time.toString()
                historyMap[Constants.FIREBASE_MESS_UPDATED_AT] = Calendar.getInstance().time.toString()
                historyMap[Constants.FIREBASE_MESS_CLOSE_AT] = ""
                historyMap[Constants.FIREBASE_MESS_DELETE_AT] = ""
                historyMap[Constants.FIREBASE_MESS_STATUS] = "active"

                firestore.collection(Constants.FIREBASE_MESS_HISTORY_DATA)
                    .document(historyKey).set(historyMap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            loader.visibility = View.GONE
                            btnAddMember.visibility = View.VISIBLE
                            Toast.makeText(this, "Entry successfully", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        }
                    }.addOnFailureListener {
                        loader.visibility = View.GONE
                        btnAddMember.visibility = View.VISIBLE
                        Toast.makeText(this, "Failed to add member", Toast.LENGTH_SHORT).show()
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