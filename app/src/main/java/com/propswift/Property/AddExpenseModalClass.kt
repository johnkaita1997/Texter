package com.propswift.Property

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.Shared.Constants.datemap
import com.propswift.Shared.Constants.expenseImageUploadList
import com.propswift.databinding.ActivityAddexpenseBinding
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.coroutines.*


//EXPENSE ACTIVITY//
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.activity_addexpense)
abstract class AddExpenseModalClass(
    var activity: Activity,
    var startForProfileImageResult: ActivityResultLauncher<Intent>
) :
    EpoxyModelWithHolder<AddExpenseModalClass.ViewHolder>() {

    private lateinit var binding: ActivityAddexpenseBinding
    var propertyid = ""

    @OptIn(DelicateCoroutinesApi::class)
    override fun bind(holder: ViewHolder) {

        binding.selectProperty.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch() {

                val listOfOwnedProperties =
                    async { activity.myViewModel(activity).getOwnedproperties() }
                val thelist = runBlocking { listOfOwnedProperties.await() }
                Log.d("-------", "initall: FOUND THE LIST TO BE ${thelist.toString()}")


                val powerMenu: PowerMenu.Builder? = PowerMenu.Builder(activity)
                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                    .setMenuRadius(10f) // sets the corner radius.
                    .setMenuShadow(10f) // sets the shadow.
                    .setWidth(900).setTextColor(ContextCompat.getColor(activity, R.color.black))
                    .setTextGravity(Gravity.CENTER)
                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                    .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                    .setSelectedMenuColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                    .setAutoDismiss(true)

                withContext(GlobalScope.coroutineContext) {
                    thelist.details.let {
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
                        propertyid = thelist.details?.get(chosenposition)!!.id.toString()
                        binding.selectProperty.setText(propertyname)
                    }
                    powerMenu?.build()?.showAsDropDown(binding.selectProperty)
                }


            }
        }

        binding.dateIncurred.setOnClickListener {
            activity.datePicker(binding.dateIncurred)
        }


        binding.saveExpense.setOnClickListener {

            if (propertyid == "") {
                activity.makeLongToast("You did not select property")
            } else {
                if (datemap.isEmpty()) {
                    activity.showAlertDialog("You did not select the date")
                } else {
                    if (binding.expenseType.text!!.isBlank()) {
                        activity.makeLongToast("You did not enter expense type")
                    } else {
                        if (binding.expenseType.text.toString().trim() != "general" && binding.expenseType.text.toString() != "incurred") {
                            activity.showAlertDialog("Expense Type can only be general or incurred")
                        } else {
                            if (binding.description.text!!.isBlank()) {
                                activity.makeLongToast("You have to enter a description")
                            } else {
                                if (binding.amount.text!!.isEmpty()) {
                                    activity.makeLongToast("You have to enter amount")
                                } else {
                                    if (binding.receipt.text!!.isEmpty()) {
                                        activity.makeLongToast("You have to enter receipt number")
                                    } else {

                                        val combined = datemap.getValue("combined")
                                        val date = combined
                                        val receipt = binding.receipt.text.toString().trim()
                                        val description = binding.description.text.toString().trim()
                                        val expenseType = binding.expenseType.text.toString().trim()
                                        val amount = binding.amount.text.toString().trim()

                                        val expenseObject = ExpenseUploadObject(amount.toInt(), date, description, expenseType, expenseImageUploadList, propertyid, receipt)
                                        CoroutineScope(Dispatchers.IO).launch() {
                                            activity.myViewModel(activity).addExpense(expenseObject)
                                            withContext(Dispatchers.Main) {
                                                activity.showAlertDialog("Expense was added successfully")
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }



                }
            }


        }


    }

    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {
            binding = ActivityAddexpenseBinding.bind(itemView)
        }

    }

}

