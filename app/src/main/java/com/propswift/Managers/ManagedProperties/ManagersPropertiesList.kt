package com.propswift.Managers.ManagedProperties

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.propswift.R
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.settingsClick
import com.propswift.databinding.ActivityManagerspropertiesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManagersPropertiesList : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityManagerspropertiesBinding
    var propertyid = ""

    private val viewmodel: MyViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerspropertiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        settingsClick(binding.include.menuicon)
        binding.include.header.setText("Managed Properties")
        binding.include.mainTabs.visibility = View.GONE


        val usernameTv = binding.username
        viewmodel.bothNames.observe(this, Observer {
            usernameTv.setText("Hi ${it}!")
        })
        CoroutineScope(Dispatchers.IO).launch() {
            viewmodel.getUserProfileDetails()
        }

        val layoutManager = LinearLayoutManager(this)
        var managedpropertiesAdapter = ManagedPropertiesAdapter(this, mutableListOf(), viewmodel)
        binding.managersRecyclerView.setLayoutManager(layoutManager)
        binding.managersRecyclerView.setLayoutManager(layoutManager)
        binding.managersRecyclerView.setAdapter(managedpropertiesAdapter)

        viewmodel.listManagedProperties.observe(this, Observer {
            managedpropertiesAdapter.updateManagedProperties(it)
        })

        getManagedProperties()

    }

    private fun getManagedProperties() {
        CoroutineScope(Dispatchers.IO).launch() {
            viewmodel.getManagedProperties()
        }
    }

    override fun onResume() {
        super.onResume()
        getManagedProperties()
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this).setTitle("Prop Swift").setCancelable(false).setMessage("Are you sure you want to exit").setIcon(R.drawable.startnow)
            .setPositiveButton("Exit", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                finish()
            }).setNegativeButton("Dismis", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }).show()
    }


}