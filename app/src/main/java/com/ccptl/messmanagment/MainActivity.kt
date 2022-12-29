package com.ccptl.messmanagment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ccptl.messmanagment.adapter.MemberListAdapter
import com.ccptl.messmanagment.member.AddMemberActivity
import com.ccptl.messmanagment.room.DemoData
import com.ccptl.messmanagment.room.DemoDatabase
import com.ccptl.messmanagment.utils.Constants
import com.ccptl.messmanagment.viewModel.DemoViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var vm: DemoViewModel

    private val memberListAdapter by lazy { MemberListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm = ViewModelProviders.of(this)[DemoViewModel::class.java]
        val firebaseFirestore = FirebaseFirestore.getInstance()



        firebaseFirestore.collection(Constants.FIREBASE_MESS_MEMBER_DATA).get().addOnSuccessListener {
            for (document in it) {
                val data = document.toObject(DemoData::class.java)
                vm.insert(data)
            }
        }


        rvMemberList.layoutManager = LinearLayoutManager(this)
        rvMemberList.adapter = memberListAdapter
        rvMemberList.setHasFixedSize(true)

        vm.getAllNotes().observe(this, Observer {
            Log.i("Notes observed", "$it")
            memberListAdapter.setMemberListData(it)
            memberListAdapter.notifyDataSetChanged()
        })

        fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity,MainActivity::class.java))
        }
    }

}