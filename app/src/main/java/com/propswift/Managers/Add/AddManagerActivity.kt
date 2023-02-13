package com.propswift.Managers.Add

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.databinding.ActivityAddManagerBinding
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class AddManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddManagerBinding
    var propertyid = ""

    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        binding.managerassignhouse.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch() {

                val listOfOwnedProperties = async { viewmodel.getOwnedproperties() }
                val thelist = viewmodel.listOfOwnedProperties.value

                Log.d("-------", "initall: FOUND THE LIST TO BE ${thelist.toString()}")

                val powerMenu: PowerMenu.Builder? = PowerMenu.Builder(this@AddManagerActivity)
                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                    .setMenuRadius(10f) // sets the corner radius.
                    .setMenuShadow(10f) // sets the shadow.
                    .setBackgroundColor(ContextCompat.getColor(this@AddManagerActivity, R.color.white))
                    .setWidth(900).setTextColor(ContextCompat.getColor(this@AddManagerActivity, R.color.black))
                    .setTextGravity(Gravity.CENTER)
                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                    .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                    .setSelectedMenuColor(ContextCompat.getColor(this@AddManagerActivity, R.color.colorPrimary))
                    .setAutoDismiss(true)

                withContext(GlobalScope.coroutineContext) {
                    thelist.let {
                        it?.forEach {
                            powerMenu?.addItem(PowerMenuItem(it.name))
                            Log.d("-------", "initall: FOUND ITEM ${it.name}")
                        }
                    }

                }

                withContext(Dispatchers.Main) {
                    powerMenu?.setOnMenuItemClickListener { position, item ->
                        val chosenposition = position
                        val propertyname = item.title.toString()
                        propertyid = thelist?.get(chosenposition)!!.id.toString()
                        binding.managerassignhouse.setText(propertyname)
                    }
                    powerMenu?.build()?.showAsDropDown(binding.managerassignhouse)
                }


            }

        }

        binding.managercreateAccount.setOnClickListener {

            Constants.mydialog = myDialog()
            showDialog(Constants.mydialog, "Adding Manager")

            if (propertyid == "") {
                makeLongToast("Please assign a property to the manager")
            } else {
                val validatelist = mutableListOf(binding.manageremail, binding.managerpassword, binding.managerconfirmpassword, binding.managerfirstname, binding.managerlastname)
                if (validated(validatelist)) {
                    val (email, password, confirmpassword, firstname, lastname) = validatelist.map { mytext(it) }
                    CoroutineScope(Dispatchers.IO).launch() {
                        viewmodel.addManager(
                            Manager(
                                confirmpassword, firstname, lastname, "", password, propertyid, email
                            )
                        )
                    }
                } else dismiss(Constants.mydialog)
            }

        }

    }
}