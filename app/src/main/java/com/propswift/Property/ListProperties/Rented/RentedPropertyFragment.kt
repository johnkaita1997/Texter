package com.propswift.Property.ListProperties

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.marwaeltayeb.progressdialog.ProgressDialog
import com.propswift.Property.ListProperties.Rented.RentedPropertyAdapter
import com.propswift.R
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.RentedDetail
import com.propswift.databinding.RentedpropertyListBinding
import com.propswift.databinding.ViewFragmentrentedBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class RentedPropertyFragment : Fragment() {

    private lateinit var viewy: View
    private var _binding: RentedpropertyListBinding? = null
    private val binding get() = _binding!!
    private lateinit var rentedProgress: ProgressDialog
    private val viewmodel: MyViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = RentedpropertyListBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        val layoutManager = LinearLayoutManager(activity)
        lateinit var rentedPropertyAdapter: RentedPropertyAdapter
        binding.rentalsRecyclerView.setLayoutManager(layoutManager)
        rentedPropertyAdapter = RentedPropertyAdapter(requireActivity(), mutableListOf(), viewmodel)
        binding.rentalsRecyclerView.setAdapter(rentedPropertyAdapter)

        binding.filter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 0) {
                    rentedPropertyAdapter.filterRentedProperties(s.toString())
                    Log.d("-------", "initall: CHECKING FOR ${s}")
                } else {
                    CoroutineScope(Dispatchers.IO).launch() {
                        viewmodel.getrentedproperties()
                    }
                }
            }
        })

        viewmodel.listofRentedProperties.observe(viewLifecycleOwner, Observer {
            rentedPropertyAdapter.updateRentedProperties(it)
        })

        CoroutineScope(Dispatchers.IO).launch() {
            viewmodel.getrentedproperties()
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
            binding.areaTv.setText("Area   :   ${item.area} ${item.area_unit}")
            binding.rentTv.setText("Rent   :   KES ${item.rent_amount}")

            itemView.setOnClickListener {
            }

        }
    }

}
