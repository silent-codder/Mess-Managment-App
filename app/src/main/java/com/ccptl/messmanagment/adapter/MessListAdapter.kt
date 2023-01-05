package com.ccptl.messmanagment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ccptl.messmanagment.R
import com.ccptl.messmanagment.room.roomModel.MessData
import com.ccptl.messmanagment.viewModel.RoomViewModel
import kotlinx.android.synthetic.main.member_list_view.view.*
import kotlinx.android.synthetic.main.member_list_view.view.tvDate
import kotlinx.android.synthetic.main.mess_history_list_view.view.*
import kotlinx.android.synthetic.main.mess_list_view.view.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class MessListAdapter : RecyclerView.Adapter<MessListAdapter.ViewHolder>() {

    private var mClickListener: ItemClickListener? = null
    private var messList = emptyList<MessData>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.mess_list_view, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvDate.text =
            getDate(messList[position].fromDateTimeStamp.toLong()) + " - " + getDate(messList[position].toDateTimeStamp.toLong())
        holder.itemView.tvMessType.text = messList[position].messType
        holder.itemView.tvPayment.text = "₹ " + messList[position].payment + "  |  " + messList[position].paymentType

        if (messList[position].paymentType.equals("Pending",true)) {
            holder.itemView.tvPayment.setTextColor(holder.itemView.context.resources.getColor(R.color.btn_red))
        } else {
            holder.itemView.tvPayment.setTextColor(holder.itemView.context.resources.getColor(R.color.green))
            holder.itemView.btnPayment.visibility = View.GONE
        }

        if (messList[position].status.equals("Active",true)) {
            holder.itemView.btnMessStatus.setTextColor(holder.itemView.context.resources.getColor(R.color.green))
        } else {
            holder.itemView.btnMessStatus.setTextColor(holder.itemView.context.resources.getColor(R.color.btn_red))
            holder.itemView.btnMessStatus.text = "Close"
        }

        holder.itemView.btnPayment.setOnClickListener {
            mClickListener?.onPaymentStatusUpdate(it, messList[position].messId)
        }

        holder.itemView.btnMessStatus.setOnClickListener {
            mClickListener?.onMessStatusUpdate(it, messList[position].messId)
        }
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onPaymentStatusUpdate(view: View?, memberId: String)
        fun onMessStatusUpdate(view: View?, memberId: String)
    }

    override fun getItemCount(): Int {
        return messList.size
    }

    fun setMessListData(data: List<MessData>) {
        this.messList = data
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