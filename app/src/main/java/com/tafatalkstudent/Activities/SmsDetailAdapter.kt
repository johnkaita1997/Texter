package com.tafatalkstudent.Activities

import android.R.attr.data
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.SmsDetail


class SmsDetailAdapter(var viewModel: MyViewModel, var activity: Activity, var etMessage: EditText) : RecyclerView.Adapter<SmsDetailAdapter.ViewHolder>() {

    lateinit var view: View
    var smsMessages: MutableList<SmsDetail> = mutableListOf()


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

        if (sms.body!!.isNotEmpty()) {

            if (sms.state == "Received") {
                val theView = holder.itemView.findViewById<TextView>(R.id.receivedTextView)
                theView.setText(sms.body)
                holder.itemView.findViewById<CardView>(R.id.receivedcard).visibility = View.VISIBLE
            } else if (sms.state == "Sent") {
                val theView = holder.itemView.findViewById<TextView>(R.id.sentTextView)
                theView.setText(sms.body)
                holder.itemView.findViewById<CardView>(R.id.sentCard).visibility = View.VISIBLE
                theView.visibility = View.VISIBLE
            } else if (sms.state == "Delivered") {
                val theView = holder.itemView.findViewById<TextView>(R.id.sentTextView)
                theView.setText(sms.body)
                holder.itemView.findViewById<CardView>(R.id.sentCard).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.sentTextView).visibility = View.VISIBLE
            } else {
                Log.d("messageDetail-------", "initall: $ - ${sms.state}")
                holder.itemView.findViewById<CardView>(R.id.sentCard).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.receivedcard).visibility = View.GONE
            }

        }
    }

    /*override fun getItemViewType(position: Int): Int {
        val sms = smsMessages[position]
        return sms.timestamp!!.toInt()
    }*/

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


    fun setData(newSmsMessages: MutableList<SmsDetail>) {
        smsMessages = newSmsMessages
        //notifyDataSetChanged()
    }

    fun updateItem(newItem: SmsDetail, recyclerView: RecyclerView) {
        try {
            smsMessages.add(newItem)
            notifyItemInserted(smsMessages.size - 1)
            recyclerView.scrollToPosition(smsMessages.size - 1)
        } catch (e: Exception) {
            Log.d("updateItem-------", "initall: AN EXCEPTION OCCURRED")
            Log.d("updateItem-------", "initall: ${e}")
        }

    }


}





