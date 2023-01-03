package com.ccptl.messmanagment.member

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ccptl.messmanagment.BuildConfig
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.room.MemberData
import com.ccptl.messmanagment.utils.Constants
import com.ccptl.messmanagment.viewModel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_member_info.*
import java.text.SimpleDateFormat
import java.util.*

class MemberInfoActivity : AppCompatActivity() {

    val TAG = "MemberInfoActivity"
    private lateinit var vm: RoomViewModel
    private var lastEntry: Boolean = false
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUserId = firebaseAuth.currentUser?.uid.toString()
    private lateinit var refreshLayout: SwipeRefreshLayout


    private lateinit var ivCall: ImageView
    private lateinit var btnPayment: Button
    private lateinit var rlMessHistory: RelativeLayout

    val data = ArrayList<MemberData>()

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_info)

        ivCall = findViewById(R.id.ivCall)
        btnPayment = findViewById(R.id.btnPayment)
        rlMessHistory = findViewById(R.id.rlMessHistory)

        findViewById<TextView>(R.id.version).text = "V-${BuildConfig.VERSION_NAME}"
        findViewById<ImageView>(R.id.ivBtnBack).setOnClickListener {
            onBackPressed()
        }

        val memberId = intent.hasExtra(Constants.FIREBASE_MESS_MEMBER_ID)

        vm = ViewModelProviders.of(this)[RoomViewModel::class.java]
        if (memberId) {
            vm.getMemberById(intent.getStringExtra(Constants.FIREBASE_MESS_MEMBER_ID)!!)
            vm.getMemberByIdData.observe(this, Observer {
                data.add(it)
                tvName.text = it.mess_member_name
                tvMobile.text = "+91 " + it.mess_member_mobile.toString()
                tvDate.text = getDate(it.mess_member_from_date.toString().toLong()) + " - " +
                        getDate(it.mess_member_to_date.toString().toLong())
                tvMessType.text = it.mess_type.toString()
                if (it.mess_member_payment_mode == "Pending") {
                    tvPayment.text =
                        "₹ " + it.mess_member_payment.toString() + " | " + it.mess_member_payment_mode
                    tvPayment.setTextColor(tvPayment.context.getColor(R.color.btn_red))
                } else {
                    tvPayment.text = "₹ " + it.mess_member_payment.toString() + " |  Done"
                    tvPayment.setTextColor(tvPayment.context.getColor(R.color.green))
                    btnPayment.visibility = View.GONE
                }
            })
        }

        ivCall.setOnClickListener {
            callMember(data[0].mess_member_mobile.toString())
        }

        btnPayment.setOnClickListener {
            firebaseFirestore.collection(Constants.FIREBASE_MESS_MEMBER_DATA)
                .document(data[0].mess_member_id.toString())
                .update("mess_member_payment_mode", "Done")
                .addOnSuccessListener {
                    btnPayment.visibility = View.GONE
                    tvPayment.text = "₹ " + data[0].mess_member_payment.toString() + " |  Done"
                    tvPayment.setTextColor(tvPayment.context.getColor(R.color.green))
                    vm.updatePaymentStatus(data[0].mess_member_id.toString(), "Done")
                    Toast.makeText(this, "Payment Done", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
                }

            val entryMap = HashMap<String, Boolean>()
            entryMap[Constants.CHECK_LAST_ENTRY_MEMBER_DATA] = true

//            firebaseFirestore.collection(Constants.FIREBASE_CHECK_LAST_ENTRY_DATA)
//                .document(firebaseAuth.currentUser?.uid.toString())
//                .set(entryMap)
        }

        rlMessHistory.setOnClickListener {
            val intent = Intent(it.context, MessHistoryActivity::class.java)
            intent.putExtra(Constants.FIREBASE_MESS_MEMBER_ID, data[0].mess_member_id.toString())
            it.context.startActivity(intent)
        }

    }

    private fun callMember(mobNumber: String) {
        //open dialer
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = "tel:$mobNumber".toUri()
        startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(milliSeconds: Long): String? {
        val formatter = SimpleDateFormat("dd MMM")
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds.toLong()
        return formatter.format(calendar.time)
    }

}