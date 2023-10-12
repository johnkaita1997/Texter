package com.tafatalkstudent.Activities

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
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

        if (sms.state == "Draft") {
            etMessage.setText(sms.body)
            sms.timestamp?.let {
                SmsDetailActivity.timestamp = it
            }
        }

        Log.d("messageDetail-------", "initall: $ - ${sms.state}")

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            setIsRecyclable(false)
        }
    }


    fun setData(newSmsMessages: List<SmsDetail>) {
        smsMessages = newSmsMessages.toMutableList()
        mainScope.launch {
            notifyDataSetChanged()
        }
    }



}