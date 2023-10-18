package com.tafatalkstudent.Activities

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.*
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale


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

        val phoneNumber = sms.phoneNumber.toString()
        val name = sms.name.toString()
        val body = if (sms.body!!.length > 40) { "${sms.body.substring(0, 40)}..." } else { sms.body }

        val isNumericOnly = isNumeric(name)
        val colorCode = generateColorCodeFromNumber(phoneNumber)
        val upperCasedName = name.substring(0, 1).uppercase(Locale.ROOT)
        Log.d("checking-------", "initall: $name")

        if (!isNumericOnly) {
            Log.d("checking-------", "Found: IsNotNumericOnly")
            holder.namedIv.setBackgroundColor(Color.parseColor(colorCode))
            holder.namedTv.text = upperCasedName
            holder.namedRL.visibility = View.VISIBLE
            holder.unnamedRL.visibility = View.GONE

        } else {
            Log.d("checking-------", "Found: IsNumericOnly")
            holder.unnamedRL.visibility = View.VISIBLE
            holder.namedRL.visibility = View.GONE
        }

        holder.contactName.text = name
        holder.messageBody.text = body
        holder.parentLinearLayout.setOnClickListener {
            activity.goToactivityIntent_Unfinished(activity, SmsDetailActivity::class.java, mapOf("phoneNumber" to phoneNumber,
                "name" to name,
                "isNumericOnly" to isNumericOnly.toString(),
                "colorCode" to colorCode,
                "upperCasedName" to upperCasedName,
            ))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namedIv: ImageView = itemView.findViewById(R.id.namedIv)
        val namedTv: TextView = itemView.findViewById(R.id.namedTv)
        val unnamedRL: RelativeLayout = itemView.findViewById(R.id.unnamedRL)
        val namedRL: RelativeLayout = itemView.findViewById(R.id.namedRL)
        val contactName: TextView = itemView.findViewById(R.id.contactName)
        val messageBody: TextView = itemView.findViewById(R.id.messageBody)
        val parentLinearLayout: LinearLayout = itemView.findViewById(R.id.parentLinearLayout)
    }

    fun setData(newSmsMessages: List<SmsDetail>) {
        smsMessages = newSmsMessages
        notifyDataSetChanged()
    }


    fun generateColorCodeFromNumber(input: String): String {
        // Remove non-numeric characters from the input string
        val numericString = input.replace(Regex("[^0-9]"), "")
        // Use the numeric string to generate a unique color code
        val colorValue = numericString.hashCode()
        val colorHex = String.format("#%06X", 0xFFFFFF and colorValue)
        return colorHex
    }


    fun isNumeric(input: String): Boolean {
        if (input[0].isDigit()) {
            return true
        }else return input[0].toString().equals("+")
    }


}