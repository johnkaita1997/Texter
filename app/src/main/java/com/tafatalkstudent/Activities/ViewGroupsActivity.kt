package com.tafatalkstudent.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.goToActivity_Unfinished
import com.tafatalkstudent.databinding.ActivityViewGroupsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewGroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewGroupsBinding
    private lateinit var adapter: ViewGroupsAdapter
    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        adapter = ViewGroupsAdapter(viewmodel, this, mutableListOf())
        val recyclerView: RecyclerView = binding.recyclerviewGroups
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setItemViewCacheSize(10000)
        recyclerView.adapter = adapter

        onclickListeners()

        fetchGroups()
    }

    private fun fetchGroups() {

        threadScope.launch {
            val _groups = async { viewmodel.getAllGroups(this@ViewGroupsActivity) }
            val groups = _groups.await()
            mainScope.launch {
                adapter.setData(groups)
            }
        }
    }

    private fun onclickListeners() {
        binding.createNewGroupButton.setOnClickListener {
            goToActivity_Unfinished(this, GroupNameAndDesc::class.java)
        }
    }

}