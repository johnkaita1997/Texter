package com.propswift.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.propswift.Dagger.TestViewModel
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        initall()
    }

    private fun initall() {

        viewmodel.bothNames.observe(this, Observer {
            binding.helloThere.setText(it)
        })
        viewmodel.totalAmount.observe(
            this, Observer {
                binding.amountSpent.setText("Amount Spent : KES ${it}")
            })
        viewmodel.getTotalNumberofReceipts.observe(this, Observer {
            binding.numberOfReceipts.setText("Number of receipts : $it")
        })


        CoroutineScope(Dispatchers.IO).launch() {
            viewmodel.getUserProfileDetails()
            viewmodel.getTotal()
            viewmodel.getTotalNumberofReceipts()
        }


        settingsClick(binding.menuicon)
        colorChanger(binding.cardone, R.color.propbrownligt, R.color.proplightgreen)
        colorChanger(binding.cardtwo, R.color.propbrownligt, R.color.proplightgreen)
        colorChanger(binding.cardthree, R.color.propbrownligt, R.color.proplightgreen)

    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this).setTitle("Prop Swift").setCancelable(false).setMessage("Are you sure you want to exit").setIcon(R.drawable.startnow)
            .setPositiveButton("Exit", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                finish()
            }).setNegativeButton("Dismis", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }).show()
    }


}

