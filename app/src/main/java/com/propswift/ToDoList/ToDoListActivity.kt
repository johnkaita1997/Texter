package com.propswift.ToDoList

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.propswift.Shared.dismissProgress
import com.propswift.Shared.goToActivity_Unfinished
import com.propswift.Shared.myViewModel
import com.propswift.databinding.ActivityToDoListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ToDoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToDoListBinding
    private var duedate = ""
    lateinit var alertDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        binding.include.header.setText("To-Do List")
        binding.include.mainTabs.visibility = View.GONE

        val layoutManager = LinearLayoutManager(this)
        lateinit var expensesAdapter: ToDoListAdapter
        binding.todoRecyclerView.setLayoutManager(layoutManager)


        CoroutineScope(Dispatchers.IO).launch() {
            val allpropertyForManagers = myViewModel(this@ToDoListActivity).getToDoList()
            withContext(Dispatchers.Main) {
                expensesAdapter = ToDoListAdapter(this@ToDoListActivity, allpropertyForManagers)
                binding.todoRecyclerView.setAdapter(expensesAdapter)
                expensesAdapter.notifyDataSetChanged()
                dismissProgress()
            }
        }


        binding.addToDoListObject.setOnClickListener {
            goToActivity_Unfinished(this, AddToDoActivity::class.java)
        }


    }

}