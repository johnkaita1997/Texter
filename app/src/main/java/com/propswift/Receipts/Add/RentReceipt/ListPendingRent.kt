package com.propswift.Receipts.Add.RentReceipt

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.propswift.Activities.ImagesAdapter
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.RentFilter
import com.propswift.Shared.makeLongToast
import com.propswift.databinding.AddRentReceiptBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ListPendingRent : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: AddRentReceiptBinding
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var propertyid: String

    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddRentReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        binding.include.header.setText("Select A Pending Rent To Pay")
        propertyid = this.intent.getStringExtra("propertyid").toString()

        val pendingRent = LinearLayoutManager(this)
        lateinit var expensesAdapter: ListPendingRentalsAdapter
        binding.expensesRecyclerView.setLayoutManager(pendingRent)
        expensesAdapter = ListPendingRentalsAdapter(this, mutableListOf())
        binding.expensesRecyclerView.setAdapter(expensesAdapter)

        viewmodel.listRentals.observe(this, Observer {
            expensesAdapter.updateRentalAdapter(it)
        })

        CoroutineScope(Dispatchers.IO).launch() {
            if (::propertyid.isInitialized) {
                viewmodel.getRentals(RentFilter(propertyid, "unpaid", null, null))
            } else {
                makeLongToast("Property Id is invalid")
            }
        }

    }


    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch() {
            if (::propertyid.isInitialized) {
                viewmodel.getRentals(RentFilter(propertyid, "unpaid", null, null))
            } else {
                makeLongToast("Property Id is invalid")
            }
        }
    }

}

