package com.propswift.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.*
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.Shared.Constants.mydialog
import com.propswift.databinding.ActivityLoginBinding
import com.propswift.databinding.ActivitySignUpBinding
import com.propswift.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WelcomeOneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {
        if (!isLoggedIn()) {
            initRecycler_Register()
        } else {
            CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {
                val e = SessionManager(this@WelcomeOneActivity).fetchu().toString()
                val p = SessionManager(this@WelcomeOneActivity).fetchp().toString()
                Log.d("-------", "initall: $e,  $p")
                myViewModel(this@WelcomeOneActivity).refreshtoken(e, p)
            }
            goToActivity(this, MainActivity::class.java)
        }
    }


    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this).setTitle("Prop Swift").setCancelable(false).setMessage("Are you sure you want to exit").setIcon(R.drawable.startnow)
            .setPositiveButton("Exit", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()

                finish()
            }).setNegativeButton("Dismis", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }).show()
    }


    private fun initRecycler_Register() {

        binding.epoxyRecyclerview.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                controller.apply {

                    /*welcomeOneModalClass {
                        id(1)
                        itemClickListener { model, parentView, clickedView, position ->}
                    }*/

                    if (intent.hasExtra("true")) {
                        LoginModalClass_(this@WelcomeOneActivity).id(0).addTo(this)
                    } else {
                        RegisterModalClass_(this@WelcomeOneActivity).id(0).addTo(this)
                    }

                }
            }
        })
    }


}


//LOGIN ACTIVITY//
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.activity_login)
abstract class LoginModalClass(var activity: Activity) : EpoxyModelWithHolder<LoginModalClass.ViewHolder>() {

    private lateinit var binding: ActivityLoginBinding

    override fun bind(holder: ViewHolder) {

        binding.dontHaveAccount.setOnClickListener {
            activity.goToActivity(activity, WelcomeOneActivity::class.java)
        }

        binding.loginbutton.setOnClickListener {

            mydialog = activity.myDialog()

            activity.showDialog(mydialog, "Logging you in")

            val validatelist = mutableListOf(binding.kaemail, binding.kapassword)
            if (activity.validated(validatelist)) {
                val (email, password) = validatelist.map { activity.mytext(it) }
                CoroutineScope(Dispatchers.IO).launch() {
                    activity.myViewModel(activity).loginuser(email, password, mydialog)
                }
            } else activity.dismiss(mydialog)

        }

    }

    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {
            binding = ActivityLoginBinding.bind(itemView)
        }
    }

}


//REGISTER ACTIVITY//
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.activity_sign_up)
abstract class RegisterModalClass(var activity: Activity) : EpoxyModelWithHolder<RegisterModalClass.ViewHolder>() {

    private lateinit var binding: ActivitySignUpBinding

    override fun bind(holder: ViewHolder) {

        binding.alreadyhaveaccount.setOnClickListener {
            activity.goToactivityIntent_Finished(activity, WelcomeOneActivity::class.java, mapOf("true" to "isaccount"))
        }

        binding.createaccount.setOnClickListener {

            mydialog = activity.myDialog()
            activity.showDialog(mydialog, "Creating your account")

            val validatelist = mutableListOf(binding.youremail, binding.yourpassword, binding.yourconfirmpassword, binding.yournameFirst, binding.yournameLast)
            if (activity.validated(validatelist)) {
                val (email, password, confirmpassword, firstname, lastname) = validatelist.map { activity.mytext(it) }
                CoroutineScope(Dispatchers.IO).launch() {
                    activity.myViewModel(activity).registeruser(email, firstname, lastname, "Middle", email, password, confirmpassword, mydialog)
                }
            } else activity.dismiss(mydialog)


        }
    }

    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {
            binding = ActivitySignUpBinding.bind(itemView)
        }
    }

}



