package com.propswift.Property

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.RadioButton
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.Shared.Constants.expenseImageUploadList
import com.propswift.databinding.ActivityAddpropertyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//REGISTER ACTIVITY//
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.activity_addproperty)
abstract class AddPropertyModalClass(var activity: Activity) : EpoxyModelWithHolder<AddPropertyModalClass.ViewHolder>() {

    private lateinit var binding: ActivityAddpropertyBinding

    override fun bind(holder: ViewHolder) {

        binding.createproperty.setOnClickListener {
            val validatelist = mutableListOf(
                binding.name, binding.location, binding.area, binding.rentAmount, binding.rentduedate
            )
            if (activity.validated(validatelist.toMutableList())) {
                val (name, location, area, rentAmount, rentduedate) = validatelist.map {
                    activity.mytext(
                        it
                    )
                }
                val radioId = binding.radioGroup.checkedRadioButtonId
                if (radioId == -1) {
                    activity.makeLongToast("You have to make a selection")
                } else {

                    val ownersip: RadioButton = activity.findViewById(radioId)
                    val isowner = ownersip.text == "   Owner"

                    CoroutineScope(Dispatchers.IO).launch() {
                        withContext(Dispatchers.Main) {
                            activity.showProgress(activity)
                        }
                        val property = CreateProperty(
                            name, location, area.toDouble(), "square meters", rentAmount, isowner, expenseImageUploadList
                        )
                        activity.myViewModel(activity).createProperty(property)
                    }

                }

            }

        }

    }

    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {
            binding = ActivityAddpropertyBinding.bind(itemView)
        }
    }

}