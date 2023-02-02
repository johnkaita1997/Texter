package com.propswift.Property.PropertyFetch

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.marwaeltayeb.progressdialog.ProgressDialog
import com.propswift.R
import com.propswift.Shared.RentedDetail
import com.propswift.Shared.dismissProgress
import com.propswift.Shared.myViewModel
import com.propswift.Shared.showAlertDialog
import com.propswift.databinding.FragmentRentedBinding
import com.propswift.databinding.ViewFragmentrentedBinding
import kotlinx.coroutines.*

class RentedPropertyFragment : Fragment() {

    private lateinit var viewy: View
    private var _binding: FragmentRentedBinding? = null
    private val binding get() = _binding!!
    private lateinit var rentedProgress: ProgressDialog


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRentedBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        val layoutManager = LinearLayoutManager(activity)
        lateinit var expensesAdapter: RentedPropertyAdapter
        binding.rentalsRecyclerView.setLayoutManager(layoutManager)

        CoroutineScope(Dispatchers.IO).launch() {
            val listOfRentedProperties = async { activity?.myViewModel(requireActivity())?.getrentedproperties() }
            withContext(Dispatchers.Main) {
                listOfRentedProperties.await()?.details.let {
                    if (it!!.isNotEmpty()) {
                        expensesAdapter = RentedPropertyAdapter(requireActivity(), it)
                        binding.rentalsRecyclerView.setAdapter(expensesAdapter)
                        expensesAdapter.notifyDataSetChanged()
                        activity?.dismissProgress()
                    }
                }
            }
        }
    }

}


@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.view_fragmentrented)
abstract class RentedModalClass(var activity: FragmentActivity?, var item: RentedDetail) : EpoxyModelWithHolder<RentedModalClass.ViewHolder>() {

    private lateinit var binding: ViewFragmentrentedBinding
    override fun bind(holder: ViewHolder) = Unit

    inner class ViewHolder : EpoxyHolder() {
        @SuppressLint("SetTextI18n")
        override fun bindView(itemView: View) {
            binding = ViewFragmentrentedBinding.bind(itemView)

            binding.propertyname.setText(item.name)
//            binding.propertynameSecond.setText(item.name)
            binding.areaTv.setText("Area   :   ${item.area} ${item.area_unit}")
            binding.rentTv.setText("Rent   :   KES ${item.rent_amount}")

            itemView.setOnClickListener {
            }

        }
    }

}
