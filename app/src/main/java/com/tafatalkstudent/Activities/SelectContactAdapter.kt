package com.tafatalkstudent.Activities

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.Contact

class SelectContactAdapter(private var contacts: MutableList<Contact>, var numberofGroupContacts: TextView, var activity: Activity, var listview: ListView, var arrayAdapter: ArrayAdapter<String>) :
    RecyclerView.Adapter<SelectContactAdapter.ViewHolder>() {

    var contactList = mutableListOf<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.display_contact_item, parent, false)
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName + " Current Adapter -> " + this::class.simpleName)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val contact = contacts[position]
        holder.nameTextView.text = contact.name
        holder.phoneNumberTextView.text = contact.phoneNumber

        if (contact in contactList) {
            holder.addContact.setBackgroundColor(Color.parseColor("#E57373"))
            holder.addContact.setText("Remove")
        } else {
            holder.addContact.setBackgroundColor(Color.parseColor("#000000"))
            holder.addContact.setText("Add")
        }

        holder.addContact.setOnClickListener {
            if (contact in contactList) {
                contactList.remove(contact)
                arrayAdapter.remove("${contact.name}  -  ${contact.phoneNumber}")
                holder.addContact.setBackgroundColor(Color.parseColor("#000000"))
                holder.addContact.setText("Add")
            } else {
                holder.addContact.setBackgroundColor(Color.parseColor("#E57373"))
                contactList.add(contact)
                arrayAdapter.insert("${contact.name}  -  ${contact.phoneNumber}", 0)
                holder.addContact.setText("Remove")
            }
            numberofGroupContacts.setText("${contactList.size} Group Contact (s)")
            SelectContactsActivity.membersList = contactList
        }

    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val addContact: Button = itemView.findViewById(R.id.addContact)
    }

    fun setSearchFilter(filter: String, newcontacts: MutableList<Contact>, fullContactList: MutableList<Contact>) {
        if (filter.isEmpty()) {
            contacts = newcontacts
        } else {
            contacts = fullContactList.filter { contact -> contact.name!!.contains(filter, ignoreCase = true) == true || contact.phoneNumber?.contains(filter) == true }.toMutableList()
        }
        notifyDataSetChanged()
    }


}
