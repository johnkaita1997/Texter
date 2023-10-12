package com.tafatalkstudent.Activities

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.*
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


data class Contact(val name: String, val phoneNumber: String)
data class SmsMessage(val body: String, val phoneNumber: String, val timestamp: Long, val type: Int)


class ContactsAdapter(var viewModel: MyViewModel, var activity: Activity, var contacts: List<Contact>, var smsMessages: List<SmsDetail>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.displaycontacts, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return smsMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sms = smsMessages[position]

        val phoneNumber = sms.phoneNumber
        holder.itemView.findViewById<TextView>(R.id.contactName).text = sms.body
        holder.itemView.findViewById<TextView>(R.id.contactmobile).text = sms.phoneNumber
        holder.itemView.findViewById<LinearLayout>(R.id.parentLinearLayout).setOnClickListener {
            activity.goToactivityIntent_Unfinished(activity, SmsDetailActivity::class.java, mapOf("phoneNumber" to phoneNumber.toString()))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun setData(newSmsMessages: List<SmsDetail>) {
        smsMessages = newSmsMessages
        mainScope.launch {
            notifyDataSetChanged()
        }
    }


}