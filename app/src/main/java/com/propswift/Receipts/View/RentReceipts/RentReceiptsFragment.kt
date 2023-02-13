package com.propswift.Receipts.View.RentReceipts

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListrentBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
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
                    viewmodel.getOtherReceipts(OtherReceiptFilter(propertyid, null, null))
                } else {
                    viewmodel.getOtherReceipts(OtherReceiptFilter(null, null, null))
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
            DoubleDateAndTimePickerDialog.Builder(activity).bottomSheet().curved().titleTextColor(Color.WHITE)
                .title("Pick Start And End Period")
                .setTab0DisplayMinutes(false)
                .setTab0DisplayHours(false)
                .setTab0DisplayDays(false)
                .setTab1DisplayMinutes(false)
                .setTab1DisplayHours(false)
                .setTab1DisplayDays(false)
                .tab0Text("Start")
                .tab1Text("End")
                .backgroundColor(Color.WHITE)
                .mainColor (activity?.resources!!.getColor(R.color.propdarkblue))
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
                                rentReceiptsDateMap["startDate"] = combinedStartDate
                                startDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                                rentReceiptsDateMap["startDate"] = combinedStartDate
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
                                val combinedStartDate = "${thisyear}-0${monthNumber}-${thisday}"
                                rentReceiptsDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                                rentReceiptsDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            }

                        }

                    }

                    binding.showing.setText("${startDate} - ${endDate}")

                    CoroutineScope(Dispatchers.IO).launch() {
                        if (::propertyid.isInitialized) {
                            viewmodel.getRentals(RentFilter(propertyid, "paid", null, null))
                        } else {
                            viewmodel.getRentals(RentFilter(null, "paid", null, null))
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
