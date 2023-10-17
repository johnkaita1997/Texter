package com.tafatalkstudent.Activities

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.SmsDetail
import kotlinx.coroutines.launch


class SmsDetailAdapter(var viewModel: MyViewModel, var activity: Activity, var smsMessages: List<SmsDetail>, var etMessage: AppCompatEditText) : RecyclerView.Adapter<SmsDetailAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.displaysmsdetail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return smsMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sms = smsMessages[position]

        Log.d("checking-------", "initall: ${sms.state} - ${sms.body} - ${sms.timestamp} - ${position}")

        val phoneNumber = sms.phoneNumber

        if (sms.state == "Received") {
            val theView = holder.itemView.findViewById<TextView>(R.id.receivedTextView)
            theView.setText(sms.body)
            holder.itemView.findViewById<CardView>(R.id.receivedcard).visibility = View.VISIBLE
        }

        if (sms.state == "Sent") {
            val theView = holder.itemView.findViewById<TextView>(R.id.sentTextView)
            theView.setText(sms.body)
            holder.itemView.findViewById<CardView>(R.id.sentCard).visibility = View.VISIBLE
        }

        if (sms.state == "Delivered") {
            val theView = holder.itemView.findViewById<TextView>(R.id.sentTextView)
            theView.setText(sms.body)
            holder.itemView.findViewById<CardView>(R.id.sentCard).visibility = View.VISIBLE
        }


        if (sms.state == "Draft") {
            etMessage.setText(sms.body)
            sms.timestamp?.let {
                SmsDetailActivity.timestamp = it
            }
            Log.d("Draft-------", "initall: Found a draft")
        }

        Log.d("messageDetail-------", "initall: $ - ${sms.state}")

    }

    override fun getItemViewType(position: Int): Int {
        val sms = smsMessages[position]
        return sms.timestamp!!.toInt()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


    fun setData(newSmsMessages: List<SmsDetail>) {
        val diffCallback = SmsDetailDiffCallback(smsMessages, newSmsMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        smsMessages = newSmsMessages.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateItem(updatedSmsDetail: SmsDetail) {
        val thelist = smsMessages as MutableList<SmsDetail>
        val oldList = ArrayList(thelist)
        val updatedIndex = thelist.indexOfFirst { it.timestamp == updatedSmsDetail.timestamp }
        if (updatedIndex != -1) {
            thelist[updatedIndex] = updatedSmsDetail  // Update the item directly in the original list
            val diffCallback = SmsDetailDiffCallback(oldList, thelist)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
        }
    }



}





class SmsDetailDiffCallback(private val oldList: List<SmsDetail>, private val newList: List<SmsDetail>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timestamp == newList[newItemPosition].timestamp
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}
