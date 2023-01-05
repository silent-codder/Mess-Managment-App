package com.ccptl.messmanagment.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ccptl.messmanagment.BuildConfig
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.adapter.MemberListAdapter
import com.ccptl.messmanagment.adapter.MessListAdapter
import com.ccptl.messmanagment.room.roomModel.MemberData
import com.ccptl.messmanagment.room.roomModel.MessData
import com.ccptl.messmanagment.utils.Constants
import com.ccptl.messmanagment.utils.PrefHelper
import com.ccptl.messmanagment.viewModel.RoomViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class InfoActivity : AppCompatActivity(),MessListAdapter.ItemClickListener {

    private lateinit var ivBtnBack: ImageView
    private lateinit var version: TextView
    private lateinit var ivProfile: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvMobile: TextView
    private lateinit var ivEdit: ImageView
    private lateinit var rvMessList: RecyclerView
    private lateinit var fabAddMessEntry: FloatingActionButton
    private lateinit var tvNoDataFound: TextView
    private lateinit var refresh: SwipeRefreshLayout

    private var MEMBER_ID: String = ""

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var prefHelper: PrefHelper
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val CURRENT_USER_ID = firebaseAuth.currentUser?.uid
    private val messListAdapter by lazy { MessListAdapter() }

    private val memberData = ArrayList<MemberData>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        ivBtnBack = findViewById(R.id.ivBtnBack)
        version = findViewById(R.id.version)
        ivProfile = findViewById(R.id.ivProfile)
        tvName = findViewById(R.id.tvName)
        tvMobile = findViewById(R.id.tvMobile)
        ivEdit = findViewById(R.id.ivEdit)
        rvMessList = findViewById(R.id.rvMessList)
        fabAddMessEntry = findViewById(R.id.fabAddMessEntry)
        tvNoDataFound = findViewById(R.id.tvNoDataFound)
        refresh = findViewById(R.id.refresh)
        roomViewModel = ViewModelProviders.of(this)[RoomViewModel::class.java]
        prefHelper = PrefHelper(this)

        ivBtnBack.setOnClickListener { onBackPressed() }
        version.text = "v" + BuildConfig.VERSION_NAME

        val memberId = intent.hasExtra("memberId")
        if (memberId != null) {
            MEMBER_ID = intent.getStringExtra("memberId").toString()
        }

        checkLastEntry()

        refresh.setOnRefreshListener {
            checkLastEntry()
        }

        roomViewModel.getMemberById(MEMBER_ID)
        roomViewModel.getMemberByIdData.observe(this) {
            if (it != null) {
                memberData.clear()
                memberData.add(it[0])
                tvName.text = it[0].name
                if (it[0].mobile.startsWith("+91")) {
                    tvMobile.text = it[0].mobile
                } else {
                    tvMobile.text = "+91 " + it[0].mobile
                }
            }
        }


        fabAddMessEntry.setOnClickListener {
            val intent = Intent(it.context, MessEntryActivity::class.java)
            intent.putExtra("memberId", memberData[0].memberId)
            startActivity(intent)
        }

        rvMessList.layoutManager = LinearLayoutManager(this)
        rvMessList.adapter = messListAdapter
        rvMessList.setHasFixedSize(true)
        messListAdapter.setClickListener(this)

        roomViewModel.getMessDataById(MEMBER_ID)
        roomViewModel.getMessDataByMemberId.observe(this) {
            if (it.isNotEmpty()) {
                tvNoDataFound.visibility = TextView.GONE
                rvMessList.visibility = RecyclerView.VISIBLE
                messListAdapter.setMessListData(it)
            } else {
                tvNoDataFound.visibility = TextView.VISIBLE
                rvMessList.visibility = RecyclerView.GONE
            }
        }

    }

    override fun onResume() {
        super.onResume()
        checkLastEntry()
    }

    private fun updateRoomDB() {
        roomViewModel.deleteAllMessData()
        fireStore.collection("messEntry")
            .whereEqualTo("createdBy", CURRENT_USER_ID).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result) {
                        val memberData = document.toObject(MessData::class.java)
                        roomViewModel.insertMessData(memberData)
                    }
                    prefHelper.put(Constants.CHECK_LAST_ENTRY_MESS_HISTORY_DATA,false)
                    Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
                    refresh.isRefreshing = false
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                refresh.isRefreshing = false
            }
    }

    private fun checkLastEntry() {
        fireStore.collection("lastEntry").document(CURRENT_USER_ID!!)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val lastEntry = it.result.getBoolean("lastEntryMessData")
                    if (lastEntry!!) {
                        updateRoomDB()
                        fireStore.collection("lastEntry").document(CURRENT_USER_ID!!)
                            .update("lastEntryMessData", false)
                        refresh.isRefreshing = false
                    } else {
                        refresh.isRefreshing = false
                    }
                }
            }
    }

    override fun onPaymentStatusUpdate(view: View?, messId: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.update_payment_status)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val rgPaymentStatus = dialog.findViewById<RadioGroup>(R.id.rgPaymentType)
        val btnUpdate = dialog.findViewById<Button>(R.id.btnUpdatePaymentStatus)

        btnUpdate.setOnClickListener {
            val selectedId = rgPaymentStatus.checkedRadioButtonId
            val radioButton = dialog.findViewById<RadioButton>(selectedId)
            val paymentStatus = radioButton.text.toString()

            fireStore.collection("messEntry").document(messId)
                .update("paymentType", paymentStatus)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                        roomViewModel.updatePaymentStatus(messId, paymentStatus)
                        roomViewModel.getMessDataByMemberId.observe(this) { it ->
                            if (it.isNotEmpty()) {
                                tvNoDataFound.visibility = TextView.GONE
                                rvMessList.visibility = RecyclerView.VISIBLE
                                messListAdapter.setMessListData(it)
                            } else {
                                tvNoDataFound.visibility = TextView.VISIBLE
                                rvMessList.visibility = RecyclerView.GONE
                            }
                        }
                        dialog.dismiss()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }

            val lastEntry = hashMapOf(
                "lastEntryMemberData" to false,
                "lastEntryMessData" to true,
            )

            fireStore.collection("lastEntry")
                .document(CURRENT_USER_ID!!)
                .set(lastEntry)

            dialog.dismiss()
        }

    }

    override fun onMessStatusUpdate(view: View?, messId: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.update_mess_status)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val rgPaymentStatus = dialog.findViewById<RadioGroup>(R.id.rgMessStatus)
        val btnUpdate = dialog.findViewById<Button>(R.id.btnUpdatePaymentStatus)

        btnUpdate.setOnClickListener {
            val selectedId = rgPaymentStatus.checkedRadioButtonId
            val radioButton = dialog.findViewById<RadioButton>(selectedId)
            val paymentStatus = radioButton.text.toString()
            fireStore.collection("messEntry").document(messId)
                .update("status", paymentStatus)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                        roomViewModel.updateMessStatus(messId, paymentStatus)
                        roomViewModel.getMessDataByMemberId.observe(this) { it ->
                            if (it.isNotEmpty()) {
                                tvNoDataFound.visibility = TextView.GONE
                                rvMessList.visibility = RecyclerView.VISIBLE
                                messListAdapter.setMessListData(it)
                            } else {
                                tvNoDataFound.visibility = TextView.VISIBLE
                                rvMessList.visibility = RecyclerView.GONE
                            }
                        }
                        dialog.dismiss()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            val lastEntry = hashMapOf(
                "lastEntryMemberData" to false,
                "lastEntryMessData" to true,
            )

            fireStore.collection("lastEntry")
                .document(CURRENT_USER_ID!!)
                .set(lastEntry)
            dialog.dismiss()
        }

    }
}