package com.tafatalkstudent.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.*
import com.tafatalkstudent.Shared.Constants.CALL_REQUEST_CODE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ContactsAdapter(
    var activity: Activity,
    var contactList: MutableList<Contact>?,
    var viewmodel: MyViewModel,
    val phoneStateReceiver: BroadcastReceiver,
    val tokenbalance: Float?,
    var equivalentminutes: Double
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    lateinit var view: View
    var job: Job? = null

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

        val updatedMobile = when {
            contactmobile.startsWith("254") -> "0${contactmobile.substring(3)}"
            contactmobile.startsWith("+254") -> "0${contactmobile.substring(4)}"
            else -> contactmobile
        }

        holder.itemView.findViewById<TextView>(R.id.contactcallbutton).setOnClickListener {
            job?.cancel()
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "${updatedMobile}"))
            activity.startActivityForResult(intent, CALL_REQUEST_CODE)
            activity.registerReceiver(phoneStateReceiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
            job = CoroutineScope(Dispatchers.IO).launch() {
                val timer = (equivalentminutes * 60 * 1000).toLong()
                delay(timer)
                hangUpCall()
            }
        }


    }


    @SuppressLint("SoonBlockedPrivateApi")
    private fun hangUpCall() {
        try {
            val telephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val method = telephonyManager.javaClass.getDeclaredMethod("getITelephony")
            method.isAccessible = true
            val telephonyService = method.invoke(telephonyManager)

            val telephonyInterface = Class.forName("com.android.internal.telephony.ITelephony")
            val endCallMethod = telephonyInterface.getDeclaredMethod("endCall")
            endCallMethod.invoke(telephonyService)
        } catch (e: Exception) {
            Log.d("-------hangUpCall", "hangUpCall: FATAL ERROR: could not connect to telephony subsystem ${e}")
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}




    fun updateContactsAdapter(contactlist: MutableList<Contact>?) {
        contactList!!.clear()
        contactList = contactlist!!
        notifyDataSetChanged()
        try {
            activity.dismissProgress()
        } catch (e: Exception) {
            Log.d("-------", "initall: ")
        }
    }



}