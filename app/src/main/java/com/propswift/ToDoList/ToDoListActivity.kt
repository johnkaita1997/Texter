package com.propswift.ToDoList

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.GetToDoListTasks_Details
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.goToActivity_Unfinished
import com.propswift.databinding.ActivityToDoListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ToDoListActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityToDoListBinding
    private var duedate = ""
    lateinit var alertDialog: AlertDialog

    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }


    private fun initall() {

        fetch()

        var isdue = false


        binding.include.header.setText("To-Do List")
        binding.include.mainTabs.visibility = View.GONE

        val layoutManager = LinearLayoutManager(this)
        lateinit var expensesAdapter: ToDoListAdapter

        findViewById<RecyclerView>(R.id.todoRecyclerView).setLayoutManager(layoutManager)
        val todoList = mutableListOf<GetToDoListTasks_Details>()
        expensesAdapter = ToDoListAdapter(this@ToDoListActivity, viewmodel)
        findViewById<RecyclerView>(R.id.todoRecyclerView).setAdapter(expensesAdapter)


        viewmodel.listOfTodoItems.observe(this, Observer {
            expensesAdapter.updateUserList(it)
            updateTextView(isdue)
        })

        viewmodel.listOfTodoItemsDueToday.observe(this, Observer {
            if (isdue) {
                expensesAdapter.updateUserList(it)
                updateTextView(isdue)
            }
        })


        binding.addToDoListObject.setOnClickListener {
            goToActivity_Unfinished(this, AddToDoActivity::class.java)
        }

        binding.alltasks.setOnClickListener {
            isdue = false
            expensesAdapter.updateUserList(viewmodel.listOfTodoItems.value!!)
            updateTextView(isdue)
        }

        binding.duetasks.setOnClickListener {
            isdue = true
            expensesAdapter.updateUserList(viewmodel.listOfTodoItemsDueToday.value!!)
            updateTextView(isdue)
        }


    }

    fun fetch() {
        lifecycleScope.launch {
            async { viewmodel.getToDoList() }
            async { viewmodel.getToDoListDueToday() }
        }
    }

    fun updateTextView(isdue: Boolean) {

        if (isdue) {
            binding.tasktype.setText("Due Today")
        } else {
            binding.tasktype.setText("All Tasks")
        }

        viewmodel.listOfTodoItems.let {
            val numberOfTodoAll = viewmodel.listOfTodoItems.value!!.size
            binding.alltaskstxt.setText("All Tasks   ${numberOfTodoAll}")
        }

        viewmodel.listOfTodoItemsDueToday.let {
            val numberOfTodoDue = viewmodel.listOfTodoItemsDueToday.value!!.size
            binding.duetaskstxt.setText("Due Today   ${numberOfTodoDue}")
        }

    }


    override fun onResume() {
        super.onResume()
        fetch()
    }

}




