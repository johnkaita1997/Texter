package com.tafatalkstudent.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.dismiss
import com.tafatalkstudent.Shared.dismissProgress
import com.tafatalkstudent.Shared.goToActivity
import com.tafatalkstudent.Shared.goToactivityIntent_Finished
import com.tafatalkstudent.Shared.makeLongToast
import com.tafatalkstudent.Shared.mytext
import com.tafatalkstudent.Shared.showProgress
import com.tafatalkstudent.Shared.validated
import com.tafatalkstudent.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        binding.alreadyhaveaccount.setOnClickListener {
            goToActivity(this, LoginActivity::class.java)
        }

        binding.createaccount.setOnClickListener {
            showProgress(this)
            val validatelist = mutableListOf(binding.youremail, binding.yourpassword, binding.yourconfirmpassword, binding.yournameFirst, binding.yournameLast, binding.mobilePhone)
            if (validated(validatelist)) {
                val email = validatelist[0].text.toString()
                val password = validatelist[1].text.toString()
                val confirmpassword = validatelist[2].text.toString()
                val firstname = validatelist[3].text.toString()
                val lastname = validatelist[4].text.toString()
                val phone = validatelist[5].text.toString()
                if (confirmpassword != password) {
                    dismissProgress()
                    makeLongToast("Passwords do not match")
                } else {
                    threadScope.launch() {
                        viewmodel.registeruser(email, firstname, lastname, password, phone, this@SignUpActivity)
                    }
                }
            } else dismissProgress()

        }
    }


}