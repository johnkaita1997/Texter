package com.propswift.Receipts.Add.OtherReceipt

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
import com.propswift.databinding.ActivityAddotherreceiptBinding
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.coroutines.*


//EXPENSE ACTIVITY//
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.activity_addotherreceipt)
abstract class AddOtherReceiptsModalClass(var activity: Activity, var startForProfileImageResult: ActivityResultLauncher<Intent>, var viewModel: MyViewModel) :
    EpoxyModelWithHolder<AddOtherReceiptsModalClass.ViewHolder>() {

    private lateinit var binding: ActivityAddotherreceiptBinding
    var propertyid = ""

    @OptIn(DelicateCoroutinesApi::class)
    override fun bind(holder: ViewHolder) {

        CoroutineScope(Dispatchers.IO).launch() {
            viewModel.getOwnedproperties()
        }

        binding.selectProperty.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch() {

                val thelist = viewModel.listOfOwnedProperties.value

                val powerMenu: PowerMenu.Builder? = PowerMenu.Builder(activity)
                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                    .setMenuRadius(10f) // sets the corner radius.
                    .setMenuShadow(10f) // sets the shadow.
                    .setWidth(900).setTextColor(ContextCompat.getColor(activity, R.color.black))
                    .setTextGravity(Gravity.CENTER)
                    .setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                    .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                    .setSelectedMenuColor(ContextCompat.getColor(activity, R.color.colorPrimary))
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
                                val description = binding.description.text.toString().trim()
                                val amount = binding.amount.text.toString().trim()
                                val receiptNumber = binding.receipt.text.toString().trim()

                                val otherReceiptObject = OtherReceiptsUploadObject(amount.toInt(), date, description, receiptNumber, expenseImageUploadList, propertyid)
                                CoroutineScope(Dispatchers.IO).launch() {
                                    viewModel.addOtherReceipt(otherReceiptObject, binding.root)
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
            binding = ActivityAddotherreceiptBinding.bind(itemView)
        }

    }

}

