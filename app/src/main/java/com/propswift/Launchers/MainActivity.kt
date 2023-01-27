package com.propswift.Launchers

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propswift.R
import com.propswift.databinding.*
import com.propswift.Shared.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {
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

