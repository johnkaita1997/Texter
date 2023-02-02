package com.propswift.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propswift.R
import com.propswift.Shared.Constants.userid
import com.propswift.Shared.Constants.username
import com.propswift.Shared.colorChanger
import com.propswift.Shared.myViewModel
import com.propswift.Shared.settingsClick
import com.propswift.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        CoroutineScope(Dispatchers.IO).launch() {
            val loggedinUser = myViewModel(this@MainActivity).getUserProfileDetails().details
            loggedinUser.let {
                withContext(Dispatchers.Main) {
                    val firstname = it?.first_name
                    val lastname = it?.last_name
                    val combined = "Hallo $firstname $lastname"
                    binding.helloThere.setText(combined)
                    userid = it?.user_id.toString()
                    username = combined
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch() {
            val totalAmount = myViewModel(this@MainActivity).getTotal()
            totalAmount.let {
                withContext(Dispatchers.Main) {
                    binding.amountSpent.setText("Amount Spent : KES ${it.details.toString()}")
                }
            }
        }


        CoroutineScope(Dispatchers.IO).launch() {
            val totalAmount = myViewModel(this@MainActivity).getTotalNumberofReceipts()
            totalAmount.let {
                withContext(Dispatchers.Main) {
                    binding.numberOfReceipts.setText("Number of receipts : ${it.details.toString()}")
                }
            }
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

