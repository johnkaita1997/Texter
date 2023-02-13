package com.propswift.Receipts.View.OtherReceipts


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.databinding.FragmentOtherreceiptsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat


@AndroidEntryPoint
class OtherReceiptsFragment : Fragment(), LifecycleOwner, MyViewModel.ActivityCallback {

    private lateinit var viewy: View
    private var _binding: FragmentOtherreceiptsBinding? = null
    private val binding get() = _binding!!
    lateinit var propertyid: String

    private val viewmodel: MyViewModel by viewModels()
    var otherRecieptsDateMap = mutableMapOf<String, String>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOtherreceiptsBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        viewmodel.setActivityCallback(this)

        if (requireActivity().intent!!.hasExtra("propertyid")) {
            propertyid = requireActivity().intent.getStringExtra("propertyid").toString()
        }

        val layoutManager = LinearLayoutManager(activity)
        lateinit var otherReceiptsAdapter: OtherReceiptsAdapter
        binding.expensesRecyclerView.setLayoutManager(layoutManager)
        otherReceiptsAdapter = OtherReceiptsAdapter(requireActivity(), mutableListOf(), viewmodel)
        binding.expensesRecyclerView.setAdapter(otherReceiptsAdapter)

        viewmodel.getOtherReceipts.observe(viewLifecycleOwner, Observer {
            otherReceiptsAdapter.updateExpenseAdapter(it)

        })

        binding.showing.setText("Receipts")
        CoroutineScope(Dispatchers.IO).launch() {
            if (::propertyid.isInitialized) {
                viewmodel.getOtherReceipts(OtherReceiptFilter(propertyid, null, null))
            } else {
                viewmodel.getOtherReceipts(OtherReceiptFilter(null, null, null))
            }
        }

        binding.clear.setOnClickListener {
            binding.showing.setText("Receipts")
            CoroutineScope(Dispatchers.IO).launch() {
                if (::propertyid.isInitialized) {
                    viewmodel.getOtherReceipts(OtherReceiptFilter(propertyid, null, null))
                } else {
                    viewmodel.getOtherReceipts(OtherReceiptFilter(null, null, null))
                }
            }
        }


        binding.monthPickerButton.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy MMM dd");
            DoubleDateAndTimePickerDialog.Builder(activity).bottomSheet().curved().titleTextColor(Color.RED)
                .title("Pick Start And End Period")
                .tab0Text("Start")
                .tab1Text("End")
                .mainColor(activity?.resources!!.getColor(R.color.propdarkblue))
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
                               otherRecieptsDateMap["startDate"] = combinedStartDate
                                startDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                               otherRecieptsDateMap["startDate"] = combinedStartDate
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
                               otherRecieptsDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                               otherRecieptsDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            }

                        }
                    }

                    binding.showing.setText("${startDate} - ${endDate}")

                    CoroutineScope(Dispatchers.IO).launch() {
                        withContext(Dispatchers.Main) {
                            if (::propertyid.isInitialized) {
                                viewmodel.getOtherReceipts(OtherReceiptFilter(propertyid, startDate, endDate))
                            } else {
                                viewmodel.getOtherReceipts(OtherReceiptFilter(null, startDate, endDate))
                            }
                        }
                    }

                }.display()
        }

    }

    override fun onDataChanged(data: Any) {
        binding.showing.setText("Receipts")
        otherRecieptsDateMap.clear()
        CoroutineScope(Dispatchers.IO).launch() {
            if (::propertyid.isInitialized) {
                viewmodel.getOtherReceipts(OtherReceiptFilter(propertyid, null, null))
            } else {
                viewmodel.getOtherReceipts(OtherReceiptFilter(null, null, null))
            }
        }
    }

}

