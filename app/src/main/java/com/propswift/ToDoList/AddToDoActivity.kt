package com.propswift.ToDoList

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.propswift.R
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.ToDoListTask
import com.propswift.Shared.makeLongToast
import com.propswift.databinding.ActivityAddToDoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddToDoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddToDoBinding
    private var duedate = ""

    val viewModel : MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddToDoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()

    }

    private fun initall() {

//        val viewModel = ViewModelProvider(this, MyViewModelFactory(this)).get(MyViewModel::class.java)

        binding.submitToDo.setOnClickListener {
            if (binding.todotext.text.isBlank()) {
                makeLongToast("Enter an item first")
            } else if (duedate == "") {
                makeLongToast("Enter a due date first")
            } else {
                CoroutineScope(Dispatchers.IO).launch() {
                    viewModel.addToDoList(ToDoListTask(duedate, binding.todotext.text.toString().trim()),
                        binding.root
                    )
                }
            }


        }

        binding.duedate.setOnClickListener {
            SingleDateAndTimePickerDialog.Builder(this).bottomSheet().curved().titleTextColor(Color.RED).displayMinutes(false).displayHours(false).displayDays(false).displayMonth(true)
                .title("Pick A Date Below")
                .mainColor(resources!!.getColor(R.color.propdarkblue))
                .backgroundColor(Color.WHITE)
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

                    val combined = "${thisyear}-${monthNumber}-${thisday}"
                    duedate = combined
                    binding.duedate.setText(combined)
                }.display()
        }

    }
}