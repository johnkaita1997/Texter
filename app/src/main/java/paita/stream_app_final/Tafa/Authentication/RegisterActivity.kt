package paita.stream_app_final.Tafa.Authentication

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import dmax.dialog.SpotsDialog
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Shared.ViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignUpActivity : AppCompatActivity() {

    var county = ""
    var agent = ""
    private lateinit var myviewmodel: ViewModel
    lateinit var mydialog: SpotsDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_sign_up)
            initall()
        } catch (e: Exception) {
            showAlertDialog(e.message.toString())
        }
    }


    private fun initall() {

        myviewmodel = ViewModel(this.application, this)
        mydialog = myDialog()

        alreadyhaveaccount.setOnClickListener {
            goToActivity(this, LoginActivity::class.java)
        }

        createaccount.setOnClickListener {

            mydialog.setMessage("Creating your account...")
            mydialog.show()

            val validatelist = mutableListOf(yourname_first, yourname_last, phoneNumber, youremail, yourconfirmpassword, yourpassword, yourschool)

            if (validated(validatelist)) {

                val firstname = yourname_first.text.toString().trim()
                val lastname = yourname_last.text.toString().trim()
                val phonenumber = phoneNumber.text.toString().trim()
                val email = youremail.text.toString().trim()
                val password = yourpassword.text.toString().trim()
                val confirmpassword = yourconfirmpassword.text.toString().trim()
                val school = yourschool.text.toString().trim()

                if (agentcode.text.toString().length <= 0) {
                    makeLongToast("Enter the agent code first")
                } else {
                    CoroutineScope(Dispatchers.IO).launch() {

                        val countyid = countyid(myViewModel(this@SignUpActivity), county)
                        val agentid = agentcode.text.toString().trim()

                        if (countyid.equals("") or agentid.equals("")) {
                            withContext(Dispatchers.Main) {
                                makeLongToast("Enter both county and registration type")
                                return@withContext
                            }
                        }
                        myViewModel(this@SignUpActivity).createUser(email, firstname, lastname, phonenumber, password, confirmpassword, countyid, agentid, school, mydialog)
                    }
                }

            } else mydialog.dismiss()

        }


        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {
            myViewModel(this@SignUpActivity).fetchAndSaveCounties(spinnerCounty)
        }


        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {}


        spinnerCounty.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item: String = parent?.getItemAtPosition(position).toString()
                county = item
            }

            override fun onNothingSelected(p0: AdapterView<*>?): Unit = TODO("Not yet implemented")

        })

    }


}


