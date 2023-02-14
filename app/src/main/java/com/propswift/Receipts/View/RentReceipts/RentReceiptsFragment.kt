package com.propswift.Receipts.View.RentReceipts

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.databinding.FragmentListrentBinding
import com.propswift.databinding.ReceiptRentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RentReceiptsFragment : Fragment(), LifecycleOwner {

    private lateinit var viewy: View
    private var _binding: FragmentListrentBinding? = null
    private val binding get() = _binding!!
    private var date = "paid"
    lateinit var propertyid: String
    private val viewmodel: MyViewModel by viewModels()
    var rentReceiptsDateMap = mutableMapOf<String, String>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListrentBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        binding.root.invalidate()
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        if (requireActivity().intent!!.hasExtra("propertyid")) {
            propertyid = requireActivity().intent.getStringExtra("propertyid").toString()
        }

        binding.showing.setText("Rents")
        val layoutManager = LinearLayoutManager(activity)
        lateinit var rentalsAdapter: RentReceiptAdapter
        binding.rentalsRecyclerView.setLayoutManager(layoutManager)
        rentalsAdapter = RentReceiptAdapter(requireActivity(), mutableListOf())
        binding.rentalsRecyclerView.setAdapter(rentalsAdapter)

        viewmodel.listRentals.observe(viewLifecycleOwner, Observer {
            rentalsAdapter.updateRentalsAdapter(it)
        })


        binding.clear.setOnClickListener {
            binding.showing.setText("Rent")
            CoroutineScope(Dispatchers.IO).launch() {
                if (::propertyid.isInitialized) {
                    viewmodel.getRentals(RentFilter(propertyid, "paid", null, null))
                } else {
                    viewmodel.getRentals(RentFilter(null, "paid", null, null))
                }
            }
        }


        CoroutineScope(Dispatchers.IO).launch() {
            if (::propertyid.isInitialized) {
                viewmodel.getRentals(RentFilter(propertyid, "paid", null, null))
            } else {
                viewmodel.getRentals(RentFilter(null, "paid", null, null))
            }
        }


        binding.monthPickerButton.setOnClickListener {

            SingleDateAndTimePickerDialog.Builder(requireActivity()).bottomSheet().curved().titleTextColor(Color.RED).displayMinutes(false).displayHours(false).displayDays(false).displayMonth(true)
                .title("Pick A Start Date").mainColor(Color.WHITE).backgroundColor(Color.WHITE).displayYears(true).displayDaysOfMonth(true).listener {

                    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                    val thisday = (if (it.date < 10) "0" else "") + it.date
                    val thismonth = monthNames.get(it.month)
                    var thisyear = it.year.toString()
                    if (thisyear.startsWith("1")) {
                        thisyear = "20${thisyear.takeLast(2)}"
                    } else {
                        thisyear = "19${thisyear}"
                    }

                    var monthNumber = ""
                    if (thismonth.equals("Jan")) monthNumber = "01"
                    else if (thismonth == "Feb") monthNumber = "02"
                    else if (thismonth == "Mar") monthNumber = "03"
                    else if (thismonth == "Apr") monthNumber = "04"
                    else if (thismonth == "May") monthNumber = "05"
                    else if (thismonth == "Jun") monthNumber = "06"
                    else if (thismonth == "Jul") monthNumber = "07"
                    else if (thismonth == "Aug") monthNumber = "08"
                    else if (thismonth == "Sep") monthNumber = "09"
                    else if (thismonth == "Oct") monthNumber = "10"
                    else if (thismonth == "Nov") monthNumber = "11"
                    else if (thismonth == "Dec") monthNumber = "12"

                    val startDate = "${thisyear}-${monthNumber}-${thisday}"

                    SingleDateAndTimePickerDialog.Builder(activity).bottomSheet().curved().titleTextColor(Color.RED).displayMinutes(false).displayHours(false).displayDays(false).displayMonth(true)
                        .title("Pick An End Date").mainColor(resources.getColor(R.color.propdarkblue)).backgroundColor(Color.DKGRAY).displayYears(true).displayDaysOfMonth(true).listener {

                            val monthNamesEnd = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                            val thisdayEnd = (if (it.date < 10) "0" else "") + it.date
                            val thismonthEnd = monthNamesEnd.get(it.month)
                            var thisyearEnd = it.year.toString()
                            if (thisyearEnd.startsWith("1")) {
                                thisyearEnd = "20${thisyearEnd.takeLast(2)}"
                            } else {
                                thisyearEnd = "19${thisyearEnd}"
                            }

                            var monthNumberEnd = ""
                            if (thismonthEnd.equals("Jan")) monthNumberEnd = "01"
                            else if (thismonthEnd == "Feb") monthNumberEnd = "02"
                            else if (thismonthEnd == "Mar") monthNumberEnd = "03"
                            else if (thismonthEnd == "Apr") monthNumberEnd = "04"
                            else if (thismonthEnd == "May") monthNumberEnd = "05"
                            else if (thismonthEnd == "Jun") monthNumberEnd = "06"
                            else if (thismonthEnd == "Jul") monthNumberEnd = "07"
                            else if (thismonthEnd == "Aug") monthNumberEnd = "08"
                            else if (thismonthEnd == "Sep") monthNumberEnd = "09"
                            else if (thismonthEnd == "Oct") monthNumberEnd = "10"
                            else if (thismonthEnd == "Nov") monthNumberEnd = "11"
                            else if (thismonthEnd == "Dec") monthNumberEnd = "12"

                            val endDate = "${thisyearEnd}-${monthNumberEnd}-${thisdayEnd}"
                            rentReceiptsDateMap["endDate"] = endDate
                            rentReceiptsDateMap["startDate"] = endDate
                            binding.showing.setText("${startDate to endDate}")

                            CoroutineScope(Dispatchers.IO).launch() {
                                if (::propertyid.isInitialized) {
                                    viewmodel.getRentals(RentFilter(propertyid, "paid", startDate, endDate))
                                } else {
                                    viewmodel.getRentals(RentFilter(null, "paid", startDate, endDate))
                                }
                            }

                        }.display()

                }.display()

        }

    }

}


@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.receipt_rent)
abstract class RentReceiptModalClass(var activity: FragmentActivity?, var item: RentDetail) : EpoxyModelWithHolder<RentReceiptModalClass.ViewHolder>() {

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

            itemView.setOnClickListener {}

        }
    }

}
