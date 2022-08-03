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
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4
import kotlin.collections.component5


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

            val validatelist = mutableListOf(yourname, youremail, yourconfirmpassword, yourpassword, yourschool)

            if (validated(validatelist)) {
                val (name, email, password, confirmpassword, school) = validatelist.map { mytext(it) }
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
                        myViewModel(this@SignUpActivity).createUser(name, email, password, confirmpassword, countyid, school, agentid, mydialog)
                    }
                }

            } else mydialog.dismiss()

        }


        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {
            myViewModel(this@SignUpActivity).fetchAndSaveCounties(spinnerCounty)
        }


        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {
        }


        spinnerCounty.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item: String = parent?.getItemAtPosition(position).toString()
                county = item
            }

            override fun onNothingSelected(p0: AdapterView<*>?): Unit = TODO("Not yet implemented")

        })

    }


}

