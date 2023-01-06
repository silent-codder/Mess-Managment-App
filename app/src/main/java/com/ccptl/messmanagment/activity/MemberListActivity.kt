package com.ccptl.messmanagment.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ccptl.messmanagment.BuildConfig
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.adapter.MemberListAdapter
import com.ccptl.messmanagment.room.roomModel.MemberData
import com.ccptl.messmanagment.utils.Constants
import com.ccptl.messmanagment.utils.PrefHelper
import com.ccptl.messmanagment.viewModel.RoomViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class MemberListActivity : AppCompatActivity() {

    private lateinit var ivBtnBack: ImageView
    private lateinit var version: TextView
    private lateinit var tvNoDataFound: TextView
    private lateinit var fabAddMember: FloatingActionButton
    private lateinit var rvMemberList: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout

    private lateinit var rlToolbar: RelativeLayout
    private lateinit var ivBtnSearch: ImageView
    private lateinit var tvBtnSearchViewCancel: TextView
    private lateinit var svSearchView: SearchView


    private lateinit var roomViewModel: RoomViewModel
    private lateinit var prefHelper: PrefHelper
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val CURRENT_USER_ID = firebaseAuth.currentUser?.uid
    private val memberListAdapter by lazy { MemberListAdapter() }
    private var data: List<MemberData> = ArrayList()

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

        tvBtnSearchViewCancel = findViewById(R.id.tvCancel)
        rlToolbar = findViewById(R.id.rlToolbar)
        ivBtnSearch = findViewById(R.id.ivBtnSearch)
        svSearchView = findViewById(R.id.searchView)

        ivBtnBack.setOnClickListener { onBackPressed() }
        version.text = "v" + BuildConfig.VERSION_NAME

        roomViewModel = ViewModelProviders.of(this)[RoomViewModel::class.java]
        prefHelper = PrefHelper(this)

        if (prefHelper.getBoolean(Constants.CHECK_LAST_ENTRY_MEMBER_DATA,false)) {
            updateRoomDB()
        }
        refresh.setOnRefreshListener {
            checkLastEntry()
        }
        checkLastEntry()

        fabAddMember.setOnClickListener {
            startActivity(Intent(this, RegisterMemberActivity::class.java))
        }

        rvMemberList.layoutManager = LinearLayoutManager(this)
        rvMemberList.adapter = memberListAdapter
        rvMemberList.setHasFixedSize(true)
        roomViewModel.getAllMemberData().observe(this, androidx.lifecycle.Observer {
            if (it.isNotEmpty()) {
                tvNoDataFound.visibility = TextView.GONE
                rvMemberList.visibility = RecyclerView.VISIBLE
                memberListAdapter.setMemberListData(it)
                data = it
            } else {
                tvNoDataFound.visibility = TextView.VISIBLE
                rvMemberList.visibility = RecyclerView.GONE
            }
        })

        ivBtnSearch.setOnClickListener {
            svSearchView.visibility = View.VISIBLE
            rlToolbar.visibility = View.GONE
            tvBtnSearchViewCancel.visibility = View.VISIBLE
        }

        tvBtnSearchViewCancel.setOnClickListener {
            svSearchView.visibility = View.GONE
            rlToolbar.visibility = View.VISIBLE
            tvBtnSearchViewCancel.visibility = View.GONE
            memberListAdapter.setMemberListData(data)
        }

        svSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchSession(query)
                } else {
                    memberListAdapter.setMemberListData(data)
                    tvNoDataFound.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    searchSession(newText)
                } else {
                    memberListAdapter.setMemberListData(data)
                    tvNoDataFound.visibility = View.GONE
                }
                return false
            }
        })

    }

    private fun searchSession(query: String) {
        val searchList: List<MemberData> = data.filter { it.name.contains(query, true) }
        if (searchList.isNotEmpty()) {
            memberListAdapter.setMemberListData(searchList)
            tvNoDataFound.visibility = View.GONE
        } else {
            memberListAdapter.setMemberListData(searchList)
            tvNoDataFound.visibility = View.VISIBLE
        }
    }

    private fun checkLastEntry() {
        fireStore.collection("lastEntry").document(CURRENT_USER_ID!!)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val lastEntry = it.result.getBoolean("lastEntryMemberData")
                    val lastEntryBoolean = lastEntry ?: false
                    if (lastEntryBoolean!!) {
                        updateRoomDB()
                        refresh.isRefreshing = false
                        fireStore.collection("lastEntry").document(CURRENT_USER_ID!!)
                            .update("lastEntryMemberData", false)
                    } else {
                        refresh.isRefreshing = false
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        checkLastEntry()
    }

    private fun updateRoomDB() {
        roomViewModel.deleteAllMemberData()
        fireStore.collection("members")
            .whereEqualTo("isDeleted", 0)
            .whereEqualTo("createdBy", CURRENT_USER_ID)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result) {
                        val memberData = document.toObject(MemberData::class.java)
                        roomViewModel.insertMember(memberData)
                    }
                    prefHelper.put(Constants.CHECK_LAST_ENTRY_MEMBER_DATA,false)
                    refresh.isRefreshing = false
//                    Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                refresh.isRefreshing = false
            }
    }

}