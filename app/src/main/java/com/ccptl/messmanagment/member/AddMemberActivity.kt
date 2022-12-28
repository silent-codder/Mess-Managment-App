package com.ccptl.messmanagment.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ccptl.messmanagment.R
import kotlinx.android.synthetic.main.activity_add_member.*

class AddMemberActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)

        btnAddMember.setOnClickListener {
            startActivity(Intent(this@AddMemberActivity,MemberInfoActivity::class.java))
        }

    }
}