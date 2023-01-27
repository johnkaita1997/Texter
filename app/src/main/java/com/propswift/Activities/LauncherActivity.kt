package com.propswift.Activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.propswift.databinding.*
import com.propswift.Shared.*

class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: LauncherActivityBinding
    lateinit var handler: Handler
    lateinit var runnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LauncherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {
        handler = Handler()
        runnable = Runnable {
            goToActivity(this, WelcomeOneActivity::class.java)
        }
        gotonextpage()
    }


    fun gotonextpage() {
        handler.postDelayed(runnable, 2000)
    }

}
