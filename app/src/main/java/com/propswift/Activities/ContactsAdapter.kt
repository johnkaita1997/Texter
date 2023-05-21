package com.propswift.Activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.Shared.Constants.CALL_REQUEST_CODE


class ContactsAdapter(var activity: Activity, var contactList: MutableList<Contact>?, var viewmodel: MyViewModel, val phoneStateReceiver: BroadcastReceiver, val tokenbalance: Float?) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.displaycontactslayout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val contactperson = contactList!!.get(position);
        val contactid = contactperson.id
        val contactmobile = contactperson.mobile
        val contactname = contactperson.name

        holder.itemView.findViewById<TextView>(R.id.contactName).setText(contactperson.name)
        holder.itemView.findViewById<TextView>(R.id.contactmobile).setText(contactperson.mobile)
        holder.itemView.findViewById<TextView>(R.id.contactrelationship).setText(contactperson.relationship)
        holder.itemView.findViewById<TextView>(R.id.contactposition).setText(position.toString())

        holder.itemView.findViewById<TextView>(R.id.contactcallbutton).setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "${contactmobile}"))
            activity.startActivityForResult(intent, CALL_REQUEST_CODE)
            activity.registerReceiver(phoneStateReceiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


    fun updateContactsAdapter(contactlist: MutableList<Contact>?) {
        contactList!!.clear()
        contactList = contactlist!!
        notifyDataSetChanged()
    }


}