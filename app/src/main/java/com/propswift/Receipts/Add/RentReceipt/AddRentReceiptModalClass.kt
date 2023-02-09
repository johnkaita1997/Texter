package com.propswift.Receipts.Add.RentReceipt

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.Shared.Constants.datemap
import com.propswift.Shared.Constants.expenseImageUploadList
import com.propswift.databinding.ActivityAddrentreceiptBinding
import kotlinx.coroutines.*


//EXPENSE ACTIVITY//
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.activity_addrentreceipt)
abstract class AddRentReceiptModalClass(var activity: Activity, var startForProfileImageResult: ActivityResultLauncher<Intent>, var viewModel: MyViewModel) :
    EpoxyModelWithHolder<AddRentReceiptModalClass.ViewHolder>() {

    private lateinit var binding: ActivityAddrentreceiptBinding
    var propertyid = ""

    @OptIn(DelicateCoroutinesApi::class)
    override fun bind(holder: ViewHolder) {

        binding.dateIncurred.setOnClickListener {
            activity.datePicker(binding.dateIncurred)
        }

        binding.saveExpense.setOnClickListener {
            if (datemap.isEmpty()) {
                activity.showAlertDialog("You did not select the date")
            } else {
                if (binding.amount.text!!.isEmpty()) {
                    activity.makeLongToast("You have to enter amount")
                } else {
                    if (binding.receipt.text!!.isEmpty()) {
                        activity.makeLongToast("You have to enter receipt number or mpesa number")
                    } else {

                        val combined = datemap.getValue("combined")

                        val date = combined
                        val amount = binding.amount.text.toString().trim()
                        val receiptNumber = binding.receipt.text.toString().trim()
                        val request_id = activity.intent.getStringExtra("requestid")
                        val propertyid = activity.intent.getStringExtra("propertyid")
                        val imagelist = expenseImageUploadList

                        CoroutineScope(Dispatchers.IO).launch() {
                            viewModel.addRent(RentPaymentModel(amount.toInt(), imagelist, date, receiptNumber, request_id.toString()), binding.root, propertyid)
                        }

                    }
                }

            }


        }


    }

    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {
            binding = ActivityAddrentreceiptBinding.bind(itemView)
        }

    }

}

