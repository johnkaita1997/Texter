package com.tafatalkstudent.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.goToActivity_Unfinished
import com.tafatalkstudent.Shared.makeLongToast
import com.tafatalkstudent.Shared.showProgress
import com.tafatalkstudent.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        binding.dontHaveAccount.setOnClickListener {
            goToActivity_Unfinished(this, SignUpActivity::class.java)
        }

        binding.loginbutton.setOnClickListener {

            val email = binding.kaemail.text.toString().trim()
            val password = binding.kapassword.text.toString().trim()

            if (email.length <= 0) {
                makeLongToast("Enter your email")
            } else if (password.length <= 0) {
                makeLongToast("Enter your password")
            } else {
                showProgress(this)
                threadScope.launch() {
                    viewmodel.loginuser(email, password, null, this@LoginActivity)
                }
            }
        }

    }

}