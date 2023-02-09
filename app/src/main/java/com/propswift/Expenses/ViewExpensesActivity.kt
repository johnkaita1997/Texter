package com.propswift.Expenses

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog
import com.propswift.R
import com.propswift.Receipts.View.OtherReceipts.OtherReceiptsAdapter
import com.propswift.Shared.Constants
import com.propswift.Shared.ExpenseFilter
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.makeLongToast
import com.propswift.databinding.ActivityViewExpensesBinding
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
class ViewExpensesActivity : AppCompatActivity(), LifecycleOwner{

    private lateinit var binding: ActivityViewExpensesBinding
    private lateinit var viewy: View
    private var _binding: FragmentExpensesBinding? = null
    private var filter = "general"
    private var date = "paid"
    lateinit var propertyid: String

    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        binding.include.header.setText("Expenses")
        if (intent!!.hasExtra("propertyid")) {
            propertyid = intent.getStringExtra("propertyid").toString()
        }

        val layoutManager = LinearLayoutManager(this)
        lateinit var expensesAdapter: ViewExpensesAdapter
        binding.expensesRecyclerView.setLayoutManager(layoutManager)
        expensesAdapter = ViewExpensesAdapter(this@ViewExpensesActivity, mutableListOf())
        binding.expensesRecyclerView.setAdapter(expensesAdapter)

        viewmodel.getExpenses.observe(this, Observer {
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
                       makeLongToast("You did not select a period")
                    }

                }.build()
            powerMenu.showAsDropDown(binding.generalIncurredBtn)
        }

        binding.monthPickerButton.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy MMM dd");
            DoubleDateAndTimePickerDialog.Builder(this).bottomSheet().curved().titleTextColor(Color.RED)
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