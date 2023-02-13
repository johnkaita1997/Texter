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
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog
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
import java.security.AccessController.getContext
import java.text.SimpleDateFormat

@AndroidEntryPoint
 class ViewExpensesActivity : AppCompatActivity(), LifecycleOwner, MyViewModel.ActivityCallback  {

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
            val dateFormat = SimpleDateFormat("yyyy MMM dd");
            DoubleDateAndTimePickerDialog.Builder(this).bottomSheet().curved().titleTextColor(Color.WHITE)
                .title("Pick Start And End Period")
                .tab0Text("Start")
                .setTab0DisplayMinutes(false)
                .setTab0DisplayHours(false)
                .setTab0DisplayDays(false)
                .setTab1DisplayMinutes(false)
                .setTab1DisplayHours(false)
                .setTab1DisplayDays(false)
                .tab1Text("End")
                .mainColor(resources!!.getColor(R.color.propdarkblue))
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
                                expenseDateMap["startDate"] = combinedStartDate
                                startDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                                expenseDateMap["startDate"] = combinedStartDate
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
                                expenseDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            } else {
                                val combinedStartDate = "${thisyear}-${monthNumber}-${thisday}"
                                expenseDateMap["endDate"] = combinedStartDate
                                endDate = combinedStartDate
                            }

                        }
                    }

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