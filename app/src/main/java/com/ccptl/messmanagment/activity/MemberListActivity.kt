package com.ccptl.messmanagment.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.ccptl.messmanagment.BuildConfig
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.room.roomModel.MemberData
import com.ccptl.messmanagment.utils.Constants
import com.ccptl.messmanagment.utils.PrefHelper
import com.ccptl.messmanagment.viewModel.RoomViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MemberListActivity : AppCompatActivity() {

    private lateinit var ivBtnBack: ImageView
    private lateinit var version: TextView
    private lateinit var tvNoDataFound: TextView
    private lateinit var fabAddMember: FloatingActionButton
    private lateinit var rvMemberList: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout


    private lateinit var roomViewModel: RoomViewModel
    private lateinit var prefHelper: PrefHelper
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val CURRENT_USER_ID = firebaseAuth.currentUser?.uid

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)

        ivBtnBack = findViewById(R.id.ivBtnBack)
        version = findViewById(R.id.version)
        tvNoDataFound = findViewById(R.id.tvNoDataFound)
        fabAddMember = findViewById(R.id.fabAddMember)
        rvMemberList = findViewById(R.id.rvMemberList)
        refresh = findViewById(R.id.refresh)

        ivBtnBack.setOnClickListener { onBackPressed() }
        version.text = "v" + BuildConfig.VERSION_NAME

        roomViewModel = ViewModelProviders.of(this)[RoomViewModel::class.java]
        prefHelper = PrefHelper(this)

        if (prefHelper.getBoolean(Constants.CHECK_LAST_ENTRY_MEMBER_DATA,false)) {
            updateRoomDB()
        }
        checkLastEntry()

        fabAddMember.setOnClickListener {
            startActivity(Intent(this, RegisterMemberActivity::class.java))
        }

    }

    private fun checkLastEntry() {
        fireStore.collection("lastEntry").document(CURRENT_USER_ID!!)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val lastEntry = it.result?.getString("lastEntryMemberData").toString()
                    if (lastEntry == "true") {
                        updateRoomDB()
                    } else {
                       Toast.makeText(this, "Up to date", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        checkLastEntry()
    }

    private fun updateRoomDB() {
        roomViewModel.deleteAll()
        fireStore.collection("members")
            .whereEqualTo("createdBy", CURRENT_USER_ID).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result) {
                        val memberData = document.toObject(MemberData::class.java)
                        roomViewModel.insertMember(memberData)
                    }
                    Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
                    prefHelper.put(Constants.CHECK_LAST_ENTRY_MEMBER_DATA,false)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

}