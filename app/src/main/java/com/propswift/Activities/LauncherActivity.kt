package com.propswift.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.propswift.Shared.*
import com.propswift.databinding.LauncherActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LauncherActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: LauncherActivityBinding
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var crash: String

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LauncherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        if (issignedIn()) {
            goToActivity(this, TestActivity::class.java)
        }

        binding.studentsignin.setOnClickListener {
            var email = binding.kaemail.text.toString().trim()
            val password = binding.kapassword.text.toString().trim()

            if (email.length <= 0) {
                makeLongToast("Enter your email")
            } else if (password.length <= 0) {
                makeLongToast("Enter your password")
            } else {
                email = email + "@gmail.com"
                CoroutineScope(Dispatchers.IO).launch() {
                    viewmodel.loginuser(email, password, null)
                }
            }
        }


    }

    private fun signInStudent() {
    }

    fun gotonextpage() {
        handler.postDelayed(runnable, 2000)
    }

}
