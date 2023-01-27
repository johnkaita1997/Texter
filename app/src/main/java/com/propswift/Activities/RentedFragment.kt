package com.propswift.Activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.epoxy.*
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.databinding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RentedFragment : Fragment() {

    private lateinit var viewy: View
    private var _binding: FragmentRentedBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O) override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRentedBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {


        CoroutineScope(Dispatchers.IO).launch() {

            val listofRentedProperties = activity?.myViewModel(requireActivity())?.getrentedproperties()

            withContext(Dispatchers.Main) {

                if (listofRentedProperties?.details!!.isEmpty()) {
                    return@withContext
                }

                binding.epoxyRecyclerview.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
                    override fun buildModels(controller: EpoxyController) {
                        controller.apply {
                            listofRentedProperties.details.forEachIndexed { index, item ->
                                RentedModalClass_(activity, item).id(index).addTo(this@apply)
                            }
                        }
                    }

                })
            }

        }


    }

}


@SuppressLint("NonConstantResourceId") @EpoxyModelClass(layout = R.layout.view_fragmentrented) abstract class RentedModalClass(var activity: FragmentActivity?, var item: RentedDetail) :
        EpoxyModelWithHolder<RentedModalClass.ViewHolder>() {

    private lateinit var binding: ViewFragmentrentedBinding
    override fun bind(holder: ViewHolder) = Unit

    inner class ViewHolder : EpoxyHolder() {
        @SuppressLint("SetTextI18n") override fun bindView(itemView: View) {
            binding = ViewFragmentrentedBinding.bind(itemView)

            binding.propertyname.setText(item.name)
            binding.propertynameSecond.setText(item.name)
            binding.areaTv.setText("Area   :   ${item.area} ${item.area_unit}")
            binding.rentTv.setText("Rent   :   KES ${item.rent_amount}")

            itemView.setOnClickListener {
                activity?.showAlertDialog(item.toString())
            }


        }
    }

}
