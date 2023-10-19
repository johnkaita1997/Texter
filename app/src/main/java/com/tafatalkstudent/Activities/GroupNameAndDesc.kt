package com.tafatalkstudent.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.goToActivity
import com.tafatalkstudent.Shared.goToactivityIntent_Unfinished
import com.tafatalkstudent.Shared.makeLongToast
import com.tafatalkstudent.Shared.validated
import com.tafatalkstudent.databinding.ActivityGroupNameAndDescBinding

class GroupNameAndDesc : AppCompatActivity() {

    private lateinit var binding: ActivityGroupNameAndDescBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupNameAndDescBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {
        onclickListeners()
    }

    private fun onclickListeners() {

        val _name = binding.groupNameEt.text
        val _description = binding.groupDescriptionEt.text

        binding.addContactsBtn.setOnClickListener {
            if (_name.isEmpty()) {
                makeLongToast("Group Name Is Compulsary")
            } else {
                val name = _name.toString().trim()
                val description = _description.toString().trim()
                goToactivityIntent_Unfinished(
                    this, SelectContactsActivity::class.java, mutableMapOf(
                        "name" to name,
                        "description" to description
                    )
                )
            }

        }
    }

}