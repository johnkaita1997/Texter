package paita.stream_app_final.Tafa.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_confirm_otp.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R

class ConfirmOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_otp)
        initall()
    }

    private fun initall() {

        val mobileNumber = intent.extras?.getString("mobile").toString()

        confirmcode.setOnClickListener {

            if (confirmcodeedittext.text.isEmpty()) {
                makeLongToast("You must enter an OTP")
            } else {
                val confirmcode = confirmcodeedittext.text.toString().trim()
                CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

                    val verifiedPhone = myViewModel(this@ConfirmOtpActivity).confirmOTP(confirmcode, mobileNumber)

                    withContext(Dispatchers.Main) {
                        if (verifiedPhone == true) {
                            goToActivity(this@ConfirmOtpActivity, MainActivity::class.java)
                        }
                    }
                }

            }
        }

    }
}