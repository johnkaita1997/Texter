package com.propswift.Expenses

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.propswift.R
import com.propswift.Shared.ExpenseFilter
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.makeLongToast
import com.propswift.databinding.ActivityViewExpensesBinding
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ViewExpensesActivity : AppCompatActivity(), LifecycleOwner, MyViewModel.ActivityCallback {

    private lateinit var binding: ActivityViewExpensesBinding
    private lateinit var viewy: View
    private var filter = "general"
    private var date = "paid"

    private val viewmodel: MyViewModel by viewModels()

    var expenseDateMap = mutableMapOf<String, String>()
    var viewexpensesPropertyId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        viewmodel.setActivityCallback(this)

        binding.include.header.setText("Expenses")
        if (intent!!.hasExtra("viewexpensesPropertyId")) {
            viewexpensesPropertyId = intent.getStringExtra("viewexpensesPropertyId").toString()
        }
        binding.showing.setText("${filter.capitalize()}")

        val layoutManager = LinearLayoutManager(this)
        lateinit var expensesAdapter: ViewExpensesAdapter
        binding.expensesRecyclerView.setLayoutManager(layoutManager)
        expensesAdapter = ViewExpensesAdapter(this@ViewExpensesActivity, mutableListOf(), viewmodel)
        binding.expensesRecyclerView.setAdapter(expensesAdapter)

        viewmodel.getExpenses.observe(this, Observer {
            expensesAdapter.updateExpenseAdapter(it, expenseDateMap, filter)

        })

        CoroutineScope(Dispatchers.IO).launch() {
            if (viewexpensesPropertyId != "") {
                viewmodel.getExpenses(ExpenseFilter(viewexpensesPropertyId, null, null, null))
            } else {
                viewmodel.getExpenses(ExpenseFilter(null, null, null, null))
            }
        }

        binding.clearselections.setOnClickListener {
            expenseDateMap.clear()
            binding.showing.setText("${filter.capitalize()}")
            CoroutineScope(Dispatchers.IO).launch() {
                withContext(Dispatchers.Main) {
                    if (viewexpensesPropertyId != "") {
                        viewmodel.getExpenses(ExpenseFilter(viewexpensesPropertyId, filter.toLowerCase(), null, null))
                    } else {
                        viewmodel.getExpenses(ExpenseFilter(null, filter.toLowerCase(), null, null))
                    }
                }
            }
        }


        binding.generalIncurredBtn.setOnClickListener {
            val powerMenu: PowerMenu = PowerMenu.Builder(this).addItem(PowerMenuItem("General", false)) // add an item.
                .addItem(PowerMenuItem("Incurred", false)) // aad an item list.
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setBackgroundColorResource(R.color.white)
                .setTextColor(ContextCompat.getColor(this, R.color.black)).setTextGravity(Gravity.CENTER).setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE).setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary)).setAutoDismiss(true)
                .setOnMenuItemClickListener { position, item ->
                    filter = item?.title.toString().lowercase()

                    if (expenseDateMap.isNotEmpty()) {

                        val startDate = expenseDateMap.get("startDate")
                        val endDate = expenseDateMap.get("endDate")

                        CoroutineScope(Dispatchers.IO).launch() {
                            binding.showing.setText("${filter.capitalize()} - ${startDate to endDate}")
                            withContext(Dispatchers.Main) {
                                if (viewexpensesPropertyId != "") {
                                    viewmodel.getExpenses(ExpenseFilter(viewexpensesPropertyId, filter.toLowerCase(), startDate, endDate))
                                } else {
                                    viewmodel.getExpenses(ExpenseFilter(null, filter.toLowerCase(), startDate, endDate))
                                }
                            }
                        }

                    } else {
                        binding.showing.setText("${filter.capitalize()}")
                        CoroutineScope(Dispatchers.IO).launch() {
                            withContext(Dispatchers.Main) {
                                if (viewexpensesPropertyId != "") {
                                    viewmodel.getExpenses(ExpenseFilter(viewexpensesPropertyId, filter.toLowerCase(), null, null))
                                } else {
                                    viewmodel.getExpenses(ExpenseFilter(null, filter.toLowerCase(), null, null))
                                }
                            }
                        }
                    }

                }.build()
            powerMenu.showAsDropDown(binding.generalIncurredBtn)
        }

        binding.monthPickerButton.setOnClickListener {

            SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .titleTextColor(Color.RED)
                .displayMinutes(false)
                .displayHours(false)
                .displayDays(false)
                .displayMonth(true)
                .title("Pick A Start Date")
                .mainColor(resources!!.getColor(R.color.propdarkblue))
                .backgroundColor(Color.DKGRAY)
                .displayYears(true)
                .displayDaysOfMonth(true)
                .listener {

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


                    SingleDateAndTimePickerDialog.Builder(this@ViewExpensesActivity)
                        .bottomSheet()
                        .curved()
                        .titleTextColor(Color.RED)
                        .displayMinutes(false)
                        .displayHours(false)
                        .displayDays(false)
                        .displayMonth(true)
                        .title("Pick An End Date")
                        .mainColor(resources!!.getColor(R.color.propdarkblue))
                        .backgroundColor(Color.DKGRAY)
                        .displayYears(true)
                        .displayDaysOfMonth(true)
                        .listener {

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
                            expenseDateMap["endDate"] = endDate
                            expenseDateMap["startDate"] = endDate
                            binding.showing.setText("${filter.capitalize()} - ${startDate to endDate}")

                            CoroutineScope(Dispatchers.IO).launch() {
                                withContext(Dispatchers.Main) {
                                    if (viewexpensesPropertyId != "") {
                                        viewmodel.getExpenses(ExpenseFilter(viewexpensesPropertyId, filter.toLowerCase(), startDate, endDate))
                                    } else {
                                        viewmodel.getExpenses(ExpenseFilter(null, filter.toLowerCase(), startDate, endDate))
                                    }
                                }
                            }

                        }.display()

                }.display()
        }
    }

    fun fullCheck() {
        if (expenseDateMap.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch() {
                if (viewexpensesPropertyId != "") {
                    viewmodel.getExpenses(ExpenseFilter(viewexpensesPropertyId, null, null, null))
                } else {
                    viewmodel.getExpenses(ExpenseFilter(null, null, null, null))
                }
            }
        } else {
            val startDate = expenseDateMap.get("startDate")
            val endDate = expenseDateMap.get("endDate")
            CoroutineScope(Dispatchers.IO).launch() {
                withContext(Dispatchers.Main) {
                    if (viewexpensesPropertyId != "") {
                        viewmodel.getExpenses(ExpenseFilter(viewexpensesPropertyId, filter.toLowerCase(), startDate, endDate))
                    } else {
                        viewmodel.getExpenses(ExpenseFilter(null, filter.toLowerCase(), startDate, endDate))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fullCheck()
    }

    override fun onDataChanged(data: Any) {
        fullCheck()
        makeLongToast("called")
    }
}