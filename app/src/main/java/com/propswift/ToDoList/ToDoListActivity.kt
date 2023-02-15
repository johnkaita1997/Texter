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

        var isdue = false

        val layoutManager = LinearLayoutManager(this)
        lateinit var expensesAdapter: ToDoListAdapter

        findViewById<RecyclerView>(R.id.todoRecyclerView).setLayoutManager(layoutManager)
        val todoList = mutableListOf<GetToDoListTasks_Details>()
        expensesAdapter = ToDoListAdapter(this@ToDoListActivity, viewmodel)
        findViewById<RecyclerView>(R.id.todoRecyclerView).setAdapter(expensesAdapter)

        viewmodel.listOfTodoItems.observe(this, Observer {
            updateTextViewAll(isdue, expensesAdapter, it)
        })

        viewmodel.listOfTodoItemsDueToday.observe(this, Observer {
            updateTextViewDue(isdue,expensesAdapter, it)
        })


        fetch()

        binding.include.header.setText("To-Do List")
        binding.include.mainTabs.visibility = View.GONE


        binding.addToDoListObject.setOnClickListener {
            goToActivity_Unfinished(this, AddToDoActivity::class.java)
        }

        binding.alltasks.setOnClickListener {
            isdue = false
            fetch()
        }

        binding.duetasks.setOnClickListener {
            isdue = true
            fetch()
        }


    }

    fun fetch() {
        lifecycleScope.launch {
            async { viewmodel.getToDoList() }
            async { viewmodel.getToDoListDueToday() }
        }
        
    }

    fun updateTextViewAll(isdue: Boolean, expensesAdapter: ToDoListAdapter, gettodolisttasksDetails: MutableList<GetToDoListTasks_Details>) {
        if (isdue) {
            binding.tasktype.setText("Due Today")
        } else {
            binding.tasktype.setText("All Tasks")
        }

        if (isdue == false) {
            viewmodel.listOfTodoItems.let {
                expensesAdapter.updateUserList(gettodolisttasksDetails)
            }
        }
        val numberOfTodoAll = viewmodel.listOfTodoItems.value!!.size
        binding.alltaskstxt.setText("All Tasks   ${numberOfTodoAll}")
    }

    fun updateTextViewDue(isdue: Boolean, expensesAdapter: ToDoListAdapter, gettodolisttasksDetails: MutableList<GetToDoListTasks_Details>) {
        if (isdue) {
            binding.tasktype.setText("Due Today")
        } else {
            binding.tasktype.setText("All Tasks")
        }
        viewmodel.listOfTodoItemsDueToday.let {
            if (isdue) {
                expensesAdapter.updateUserList(gettodolisttasksDetails)
            }
            val numberOfTodoDue = viewmodel.listOfTodoItemsDueToday.value!!.size
            binding.duetaskstxt.setText("Due Today   ${numberOfTodoDue}")
        }
    }


    override fun onResume() {
        super.onResume()
        fetch()
    }

}




