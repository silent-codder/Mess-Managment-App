package com.ccptl.messmanagment.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.member.MemberInfoActivity
import com.ccptl.messmanagment.room.MemberData
import com.ccptl.messmanagment.room.MessHistoryData
import com.ccptl.messmanagment.utils.Constants
import kotlinx.android.synthetic.main.member_list_view.view.*
import kotlinx.android.synthetic.main.member_list_view.view.rlMain
import kotlinx.android.synthetic.main.member_list_view.view.tvDate
import kotlinx.android.synthetic.main.mess_history_list_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessHistoryListAdapter: RecyclerView.Adapter<MessHistoryListAdapter.ViewHolder>() {
    private var messHistoryData = emptyList<MessHistoryData>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mess_history_list_view,parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvStatus.text = messHistoryData[position].mess_member_status
        holder.itemView.tvDate.text = getDate(messHistoryData[position].mess_member_from_date.toLong()) +" - "+ getDate(messHistoryData[position].mess_member_to_date.toLong())
    }

    override fun getItemCount(): Int {
        return messHistoryData.size
    }

    fun setMessHistoryListData(data: List<MessHistoryData>){
        this.messHistoryData = data
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