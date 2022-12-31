package com.ccptl.messmanagment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ccptl.messmanagment.adapter.MemberListAdapter
import com.ccptl.messmanagment.member.AddMemberActivity
import com.ccptl.messmanagment.room.MemberData
import com.ccptl.messmanagment.utils.Constants
import com.ccptl.messmanagment.viewModel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var vm: RoomViewModel
    private var lastEntry: Boolean = false
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUserId = firebaseAuth.currentUser?.uid.toString()
    private val memberListAdapter by lazy { MemberListAdapter() }
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm = ViewModelProviders.of(this)[RoomViewModel::class.java]
        refreshLayout = findViewById(R.id.refresh)

        version.text = "V : ${BuildConfig.VERSION_NAME}"

        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            checkUpdate()
        }

        checkUpdate()

        rvMemberList.layoutManager = LinearLayoutManager(this)
        rvMemberList.adapter = memberListAdapter
        rvMemberList.setHasFixedSize(true)

        vm.getAllNotes().observe(this, Observer {
            Log.i("Notes observed", "$it")
            memberListAdapter.setMemberListData(it)
            memberListAdapter.notifyDataSetChanged()
        })

        fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddMemberActivity::class.java))
        }
    }

    private fun checkUpdate() {
        firebaseFirestore.collection(Constants.FIREBASE_CHECK_LAST_ENTRY_DATA)
            .document(currentUserId)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.get(Constants.CHECK_LAST_ENTRY_MEMBER_DATA)
                    if (data != null) {
                        lastEntry = data as Boolean
                        if (lastEntry) {
                            vm.deleteAllNotes()
                            fetchData()
                            Log.d(TAG, "LAST ENTRY $data : MEMBER DATA FOUND")
                        }else{
                            Log.d(TAG, "LAST ENTRY $data : MEMBER DATA NOT FOUND")
                            Toast.makeText(this, "UP TO DATE", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Log.d(TAG, "LAST ENTRY $data : MEMBER DATA NOT FOUND")
                        Toast.makeText(this, "UP TO DATE", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "LAST ENTRY : MEMBER DATA NOT FOUND")
                Toast.makeText(this, "UP TO DATE", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchData() {
        Log.d(TAG, "FIREBASE  : fetch data from firebase")
        firebaseFirestore.collection(Constants.FIREBASE_MESS_MEMBER_DATA)
            .whereEqualTo(Constants.RESTAURANT_ID, currentUserId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result) {
                        val memberData = document.toObject(MemberData::class.java)
                        vm.insert(memberData)
                    }
                }
            }

        firebaseFirestore.collection(Constants.FIREBASE_CHECK_LAST_ENTRY_DATA)
            .document(currentUserId)
            .update(Constants.CHECK_LAST_ENTRY_MEMBER_DATA, false)
    }

}