package com.tafatalkstudent.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.tafatalkstudent.Dagger.TestViewModel
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.*
import com.tafatalkstudent.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityMainBinding

    @Inject
    @Named("carname")
    lateinit var carname: String

    private val testviewModel: TestViewModel by viewModels()
    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName)
        initall()
    }

    private fun initall() {
        viewmodel.bothNames.observe(this, Observer { binding.helloThere.setText(it) })
        viewmodel.totalAmount.observe(this, Observer { binding.amountSpent.setText("Amount Spent : KES ${it}") })
        viewmodel.getTotalNumberofReceipts.observe(this, Observer {
            binding.numberOfReceipts.setText("Number of receipts : $it")
        })

//        CoroutineScope(Dispatchers.IO).launch() {
//            viewmodel.getUserProfileDetails()
//            viewmodel.getTotal()
//            viewmodel.getTotalNumberofReceipts()
//        }

        colorChanger(binding.cardone, R.color.propbrownligt, R.color.proplightgreen)
        colorChanger(binding.cardtwo, R.color.propbrownligt, R.color.proplightgreen)
        colorChanger(binding.cardthree, R.color.propbrownligt, R.color.proplightgreen)

    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this).setTitle("Prop Swift").setCancelable(false).setMessage("Are you sure you want to exit").setIcon(R.drawable.logodark)
            .setPositiveButton("Exit", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                finish()
            }).setNegativeButton("Dismis", DialogInterface.OnClickListener {
                    dialog, _ -> dialog.dismiss()
            })
            .show()
    }


}

