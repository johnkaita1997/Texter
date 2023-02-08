package com.propswift.Receipts


import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.databinding.FragmentExpensesBinding
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat


@AndroidEntryPoint
class ExpensesFragment : Fragment(), LifecycleOwner {

    private lateinit var viewy: View
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    private var filter = "general"
    private var date = "paid"
    lateinit var propertyid: String

    private val viewmodel: MyViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpensesBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        if (requireActivity().intent!!.hasExtra("propertyid")) {
            propertyid = requireActivity().intent.getStringExtra("propertyid").toString()
        }

        val layoutManager = LinearLayoutManager(activity)
        lateinit var expensesAdapter: ExpensesAdapter
        binding.expensesRecyclerView.setLayoutManager(layoutManager)
        expensesAdapter = ExpensesAdapter(requireActivity(), mutableListOf())
        binding.expensesRecyclerView.setAdapter(expensesAdapter)

        viewmodel.getExpenses.observe(viewLifecycleOwner, Observer {
            expensesAdapter.updateExpenseAdapter(it)

        })

        CoroutineScope(Dispatchers.IO).launch() {
            if (::propertyid.isInitialized) {
                viewmodel.getExpenses(ExpenseFilter(propertyid, null, null, null))
            } else {
                viewmodel.getExpenses(ExpenseFilter(null, null, null, null))
            }
        }


        binding.generalIncurredBtn.setOnClickListener {
            val powerMenu: PowerMenu = PowerMenu.Builder(requireContext()).addItem(PowerMenuItem("General", false)) // add an item.
                .addItem(PowerMenuItem("Incurred", false)) // aad an item list.
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black)).setTextGravity(Gravity.CENTER).setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE).setSelectedMenuColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary)).setAutoDismiss(true)
                .setOnMenuItemClickListener { position, item ->
                    filter = item?.title.toString().lowercase()

                    if (Constants.expenseDateMap.isNotEmpty()) {
                        Log.d("-------", "initall: It is not empty")
                        val startDate = Constants.expenseDateMap.get("startDate")
                        val endDate = Constants.expenseDateMap.get("endDate")

                        CoroutineScope(Dispatchers.IO).launch() {
                            withContext(Dispatchers.Main) {
                                if (::propertyid.isInitialized) {
                                    viewmodel.getExpenses(ExpenseFilter(propertyid, filter.toLowerCase(), startDate, endDate))
                                } else {
                                    viewmodel.getExpenses(ExpenseFilter(null, filter.toLowerCase(), startDate, endDate))
                                }
                            }
                        }

                    } else {
                        activity?.makeLongToast("You did not select a period")
                    }

                }.build()
            powerMenu.showAsDropDown(binding.generalIncurredBtn)
        }

        binding.monthPickerButton.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy MMM dd");
            DoubleDateAndTimePickerDialog.Builder(activity).bottomSheet().curved().titleTextColor(Color.RED)
                .title("Pick Start And End Period")
                .tab0Text("Start")
                .tab1Text("End")
                .mainColor(Color.RED).backgroundColor(Color.WHITE)
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
                                val combinedStartDate = "${thisyear}-0${monthNumber}-${thisday}"
                                Constants.expenseDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                                Constants.expenseDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            }

                        }
                    }

                    binding.monthPickerButton.setText("${startDate} - ${endDate}")

                    CoroutineScope(Dispatchers.IO).launch() {
                        withContext(Dispatchers.Main) {
                            if (::propertyid.isInitialized) {
                                viewmodel.getExpenses(ExpenseFilter(propertyid, filter.toLowerCase(), startDate, endDate))
                            } else {
                               viewmodel.getExpenses(ExpenseFilter(null, filter.toLowerCase(), startDate, endDate))
                            }
                        }
                    }

                }.display()
        }

    }

}

