package com.ccptl.messmanagment.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.member.MemberInfoActivity
import com.ccptl.messmanagment.room.DemoData
import kotlinx.android.synthetic.main.member_list_view.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MemberListAdapter: RecyclerView.Adapter<MemberListAdapter.ViewHolder>() {
    private var memberList = emptyList<DemoData>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.member_list_view,parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        holder.itemView.tvSrNo.text = (position + 1).toString()

        if (position % 2 == 0){
            holder.itemView.rlMain.background = holder.itemView.context.getDrawable(R.color.white_grey)
            holder.itemView.rlvTitle.backgroundColor = holder.itemView.context.resources.getColor(R.color.gray)
        } else{
            holder.itemView.rlMain.background = holder.itemView.context.getDrawable(R.color.gray)
            holder.itemView.rlvTitle.backgroundColor = holder.itemView.context.resources.getColor(R.color.white_grey)
        }

        holder.itemView.tvName.text = memberList[position].mess_member_name
        holder.itemView.rlvTitle.titleText = memberList[position].mess_member_name.substring(0,1)
        holder.itemView.tvMobile.text = "+91 " + memberList[position].mess_member_mobile
        holder.itemView.tvDate.text = getDate(memberList[position].mess_member_from_date.toLong()) +" - "+ getDate(memberList[position].mess_member_to_date.toLong())

        holder.itemView.rlMain.setOnClickListener {
            val intent = Intent(it.context, MemberInfoActivity::class.java)
//            intent.putExtra(Constants.FIREBASE_MESS_MEMBER_ID, memberList[position].mess_member_id)
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    fun setMemberListData(data: List<DemoData>){
        this.memberList = data
        notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(milliSeconds: Long): String? {
        val formatter = SimpleDateFormat("dd MMM")
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds.toLong()
        return formatter.format(calendar.time)
    }
}