package com.tafatalkstudent.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony.Sms
import android.util.Log
import androidx.activity.viewModels
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.Constants
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.SimCard
import com.tafatalkstudent.Shared.goToActivity
import com.tafatalkstudent.Shared.goToActivity_Unfinished
import com.tafatalkstudent.databinding.ActivityLandingPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingPage : AppCompatActivity() {
    
    private lateinit var binding: ActivityLandingPageBinding
    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName)
        initall()
    }

    private fun initall() {
        setUpActiveSimCardIfNotExisting()
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

    private fun setUpActiveSimCardIfNotExisting() {
        GlobalScope.launch {
            viewmodel.insertActiveSimCard(SimCard(0, 1), this@LandingPage)
        }
    }
}