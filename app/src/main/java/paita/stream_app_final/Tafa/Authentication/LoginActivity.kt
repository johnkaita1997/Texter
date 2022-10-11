package paita.stream_app_final.Tafa.Authentication


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.WebViewActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var mydialog: SpotsDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initall()
    }

    private fun initall() {

        mydialog = myDialog()

        dontHaveAccount.setOnClickListener {
            goToActivity(this, SignUpActivity::class.java)
        }

        loginbutton.setOnClickListener {

            mydialog.setMessage("Logging you in")
            mydialog.show()

            val validatelist = mutableListOf(kaemail, kapassword)
            if (validated(validatelist)) {
                val (email, password) = validatelist.map { mytext(it) }
                CoroutineScope(Dispatchers.IO).launch() {
                    myViewModel(this@LoginActivity).loginuser(email, password, mydialog)
                }
            } else mydialog.dismiss()

        }


        forgotpassword.setOnClickListener {
            goToActivity_Unfinished(this, WebViewActivity::class.java)
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}