package com.tafatalkstudent.Activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.Contact
import com.tafatalkstudent.Shared.Groups
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.goToActivity
import com.tafatalkstudent.Shared.makeLongToast
import com.tafatalkstudent.databinding.ActivitySelectContactsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectContactsActivity : AppCompatActivity() {
    private lateinit var fullContactList: MutableList<Contact>
    private lateinit var binding: ActivitySelectContactsBinding
    private lateinit var numberofGroupContacts: TextView
    private lateinit var description: String
    private lateinit var name: String
    private val viewmodel: MyViewModel by viewModels()


    companion object{
        var membersList = mutableListOf<Contact>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName)
        initall()
    }

    private fun initall() {

        name = intent.getStringExtra("name").toString()
        description = intent.getStringExtra("description").toString()

        val listview = binding.listView
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mutableListOf())
        listview.adapter = arrayAdapter
        numberofGroupContacts = binding.numberofGroupContacts

        fullContactList = getContacts(this)
        val selectContactAdapter = SelectContactAdapter(fullContactList, numberofGroupContacts,this@SelectContactsActivity, listview, arrayAdapter)
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.contactsRecyclerView.setItemViewCacheSize(1000)
        binding.contactsRecyclerView.adapter = selectContactAdapter

        onclickListeners(fullContactList, selectContactAdapter)

    }

    private fun onclickListeners(contacts: MutableList<Contact>, selectContactAdapter: SelectContactAdapter) {

        binding.seachContactEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                if (newText.isEmpty()) {
                    selectContactAdapter.setSearchFilter(newText, contacts, fullContactList)
                } else {
                    selectContactAdapter.setSearchFilter(newText, mutableListOf(), fullContactList)
                }
            }
        })

        binding.saveGroup.setOnClickListener {
            val groupname = name
            val groupDescription = description
            val groupId = null
            val members = membersList
            val group = Groups(groupId, groupname, groupDescription, members)
            threadScope.launch {
                val _inserted = async {  viewmodel.insertGroup(group, this@SelectContactsActivity) }
                val inserted = _inserted.await()
                if (inserted != null) {
                    mainScope.launch {
                        val saveGroup = async { viewmodel.saveGroup(inserted, this@SelectContactsActivity) }
                        saveGroup.await()
                        makeLongToast("Group Created Successfully")
                        goToActivity(this@SelectContactsActivity, ViewGroupsActivity::class.java)
                    }
                }
            }
        }


    }

    @SuppressLint("Range")
    fun getContacts(context: Context): MutableList<Contact> {
        val contactsList = mutableListOf<Contact>()
        val uniquePhoneNumbers = HashSet<String>() // HashSet to store unique phone numbers

        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                // Check if the phone number is unique before adding it to the list
                if (uniquePhoneNumbers.add(phoneNumber)) {
                    contactsList.add(Contact(name, phoneNumber))
                }
            }
        }
        return contactsList
    }



}