package com.propswift.Receipts

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.Shared.Constants.rentalDateMap
import com.propswift.databinding.FragmentRentedBinding
import com.propswift.databinding.ReceiptRentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RentalsFragment : Fragment() {

    private lateinit var viewy: View
    private var _binding: FragmentRentedBinding? = null
    private val binding get() = _binding!!
    private var date = "paid"
    lateinit var propertyid: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRentedBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        if (requireActivity().intent!!.hasExtra("propertyid")) {
            propertyid = requireActivity().intent.getStringExtra("propertyid").toString()
        }

        val layoutManager = LinearLayoutManager(activity)
        lateinit var expensesAdapter: RentAdapter
        binding.rentalsRecyclerView.setLayoutManager(layoutManager)


        CoroutineScope(Dispatchers.IO).launch() {
            withContext(Dispatchers.Main) {
//                activity?.showProgress(requireActivity())
            }

            if (::propertyid.isInitialized) {
                val allRentals = activity?.myViewModel(requireActivity())?.getRentals(RentFilter(propertyid, "paid", null, null))
                withContext(Dispatchers.Main) {
                    expensesAdapter = RentAdapter(requireActivity(), allRentals)
                    binding.rentalsRecyclerView.setAdapter(expensesAdapter)
                    expensesAdapter.notifyDataSetChanged()
                    activity?.dismissProgress()
                }
            } else {
                val allRentals = activity?.myViewModel(requireActivity())?.getRentals(RentFilter(null, "paid", null, null))
                withContext(Dispatchers.Main) {
                    expensesAdapter = RentAdapter(requireActivity(), allRentals)
                    binding.rentalsRecyclerView.setAdapter(expensesAdapter)
                    expensesAdapter.notifyDataSetChanged()
                    activity?.dismissProgress()
                }
            }

        }


        binding.datePickerButton.setOnClickListener {

            DoubleDateAndTimePickerDialog.Builder(activity).bottomSheet().curved().titleTextColor(Color.RED)
                .title("Pick Start And End Period")
                .tab0Text("Start")
                .tab1Text("End")
                .mainColor(Color.RED)
                .backgroundColor(Color.WHITE)
                .listener {

                    var startDate = ""
                    var endDate = ""

                    it.forEachIndexed { index, date ->
                        if (index == 0) {
                            val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                            val thisday = (if (date.date < 10) "0" else "") + date.date
                            val thismonth = monthNames.get(date.month)
                            var thisyear = date.year.toString()
                            if (thisyear.startsWith("1")) {
                                thisyear = "20${thisyear.takeLast(2)}"
                            } else {
                                thisyear = "19${thisyear}"
                            }
                            var monthNumber = 0
                            if (thismonth.equals("Jan")) monthNumber = 1
                            else if (thismonth == "Feb") monthNumber = 2
                            else if (thismonth == "Mar") monthNumber = 3
                            else if (thismonth == "Apr") monthNumber = 4
                            else if (thismonth == "May") monthNumber = 5
                            else if (thismonth == "Jun") monthNumber = 6
                            else if (thismonth == "Jul") monthNumber = 7
                            else if (thismonth == "Aug") monthNumber = 8
                            else if (thismonth == "Sep") monthNumber = 9
                            else if (thismonth == "Oct") monthNumber = 10
                            else if (thismonth == "Nov") monthNumber = 11
                            else if (thismonth == "Dec") monthNumber = 12

                            if (monthNumber < 10) {
                                val combinedStartDate = "${thisyear}-0${monthNumber}-${thisday}"
                                Constants.expenseDateMap["startDate"] = combinedStartDate
                                startDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                                Constants.expenseDateMap["startDate"] = combinedStartDate
                                startDate = combinedStartDate
                            }

                        } else {
                            val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                            val thisday = (if (date.date < 10) "0" else "") + date.date
                            val thismonth = monthNames.get(date.month)
                            var thisyear = date.year.toString()
                            if (thisyear.startsWith("1")) {
                                thisyear = "20${thisyear.takeLast(2)}"
                            } else {
                                thisyear = "19${thisyear}"
                            }
                            var monthNumber = 0
                            if (thismonth.equals("Jan")) monthNumber = 1
                            else if (thismonth == "Feb") monthNumber = 2
                            else if (thismonth == "Mar") monthNumber = 3
                            else if (thismonth == "Apr") monthNumber = 4
                            else if (thismonth == "May") monthNumber = 5
                            else if (thismonth == "Jun") monthNumber = 6
                            else if (thismonth == "Jul") monthNumber = 7
                            else if (thismonth == "Aug") monthNumber = 8
                            else if (thismonth == "Sep") monthNumber = 9
                            else if (thismonth == "Oct") monthNumber = 10
                            else if (thismonth == "Nov") monthNumber = 11
                            else if (thismonth == "Dec") monthNumber = 12

                            if (monthNumber < 10) {
                                var combinedStartDate = "${thisyear}-0${monthNumber}-${thisday}"
                                Constants.expenseDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                                Constants.expenseDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            }

                        }

                    }

                    binding.datePickerButton.setText("${startDate} - ${endDate}")

                    CoroutineScope(Dispatchers.IO).launch() {
                        val allRentals = activity?.myViewModel(requireActivity())?.getRentals(RentFilter(null, "paid", startDate, endDate))
                        withContext(Dispatchers.Main) {
                            expensesAdapter = RentAdapter(requireActivity(), allRentals)
                            binding.rentalsRecyclerView.setAdapter(expensesAdapter)
                            expensesAdapter.notifyDataSetChanged()
                            activity?.dismissProgress()
                        }
                    }

                }.display()

        }

    }

}


@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.receipt_rent)
abstract class RentReceiptModalClass(var activity: FragmentActivity?, var item: RentDetail) :
    EpoxyModelWithHolder<RentReceiptModalClass.ViewHolder>() {

    private lateinit var binding: ReceiptRentBinding
    override fun bind(holder: ViewHolder) = Unit

    inner class ViewHolder : EpoxyHolder() {
        @SuppressLint("SetTextI18n")
        override fun bindView(itemView: View) {
            binding = ReceiptRentBinding.bind(itemView)

            binding.rentDateTv.setText(item.date_paid)
            binding.rentreceiptNoTv.setText(item.id)
            binding.rentAmountTv.setText("Amount Paid   :   KES 50,000")
            binding.propertyName.setText(item.property.name)

            itemView.setOnClickListener {
            }


        }
    }

}
