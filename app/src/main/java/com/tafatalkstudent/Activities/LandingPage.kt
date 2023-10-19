package com.tafatalkstudent.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony.Sms
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.goToActivity
import com.tafatalkstudent.Shared.goToActivity_Unfinished
import com.tafatalkstudent.databinding.ActivityLandingPageBinding

class LandingPage : AppCompatActivity() {
    
    private lateinit var binding: ActivityLandingPageBinding

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {
        onClicklisteners()
    }

    private fun onClicklisteners() {
        binding.viewGroups.setOnClickListener { 
              goToActivity_Unfinished(this, ViewGroupsActivity::class.java)
        }
        binding.viewMessages.setOnClickListener {
              goToActivity_Unfinished(this, SmsActivity::class.java)
        }
        binding.sendBulkSmsButton.setOnClickListener {
              goToActivity_Unfinished(this, ViewGroupsActivity::class.java)
        }
    }
}