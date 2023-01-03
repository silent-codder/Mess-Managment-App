package com.ccptl.messmanagment.member

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ccptl.messmanagment.BuildConfig
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.adapter.MessHistoryListAdapter
import com.ccptl.messmanagment.room.MessHistoryData
import com.ccptl.messmanagment.utils.Constants
import com.ccptl.messmanagment.viewModel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.version
import kotlinx.android.synthetic.main.activity_mess_history.*

class MessHistoryActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var vm: RoomViewModel
    private var lastEntry: Boolean = false
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUserId = firebaseAuth.currentUser?.uid.toString()
    private val messHistoryListAdapter by lazy { MessHistoryListAdapter() }
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mess_history)

        vm = ViewModelProviders.of(this)[RoomViewModel::class.java]
        refreshLayout = findViewById(R.id.refresh)

        version.text = "V : ${BuildConfig.VERSION_NAME}"

        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            checkUpdate()
        }

        checkUpdate()

        rvHistoryList.layoutManager = LinearLayoutManager(this)
        rvHistoryList.adapter = messHistoryListAdapter
        rvHistoryList.setHasFixedSize(true)

        vm.getAllMessHistoryData().observe(this, Observer {
            Log.i("Notes observed", "$it")
            messHistoryListAdapter.setMessHistoryListData(it)
            messHistoryListAdapter.notifyDataSetChanged()
        })

        fetchData()

    }

    private fun checkUpdate() {
        firebaseFirestore.collection(Constants.FIREBASE_CHECK_LAST_ENTRY_DATA)
            .document(currentUserId)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.get(Constants.CHECK_LAST_ENTRY_MESS_HISTORY_DATA)
                    if (data != null) {
                        lastEntry = data as Boolean
                        if (lastEntry) {
                            vm.deleteAllNotes()
                            fetchData()
                            Log.d(TAG, "LAST ENTRY $data : MESS HISTORY DATA FOUND")
                        }else{
                            Log.d(TAG, "LAST ENTRY $data : MESS HISTORY DATA NOT FOUND")
                            Toast.makeText(this, "UP TO DATE", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Log.d(TAG, "LAST ENTRY $data : MESS HISTORY DATA NOT FOUND")
                        Toast.makeText(this, "UP TO DATE", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "LAST ENTRY : MESS HISTORY DATA NOT FOUND")
                Toast.makeText(this, "UP TO DATE", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchData() {
        Log.d(TAG, "FIREBASE  : fetch data from firebase")
        firebaseFirestore.collection(Constants.FIREBASE_MESS_HISTORY_DATA)
            .whereEqualTo(Constants.FIREBASE_MESS_HISTORY_MEMBER_ID, intent.getStringExtra(Constants.FIREBASE_MESS_MEMBER_ID)!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result) {
                        val messHistoryData = document.toObject(MessHistoryData::class.java)
                        vm.insertHistory(messHistoryData)
                    }
                }
            }

        firebaseFirestore.collection(Constants.FIREBASE_CHECK_LAST_ENTRY_DATA)
            .document(currentUserId)
            .update(Constants.CHECK_LAST_ENTRY_MESS_HISTORY_DATA, false)
    }

}