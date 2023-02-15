package com.propswift.ToDoList

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.GetToDoListTasks_Details
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.dateDifference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ToDoListAdapter(var activity: FragmentActivity, var viewModel: MyViewModel) : RecyclerView.Adapter<ToDoListAdapter.ViewHolder>() {

    var todolistList = mutableListOf<GetToDoListTasks_Details>()
    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_todolist, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todolistList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val todoListObject = todolistList.get(position);
        val todolistid = todoListObject.id
        val todolistname = todoListObject.title

        holder.itemView.findViewById<TextView>(R.id.todolisttext).setText(todolistname)

        val duedate = todoListObject.due_date.toString()
        val todaydate: String = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val duein = dateDifference(duedate, todaydate)

        if (duein > 0) {
            holder.itemView.findViewById<TextView>(R.id.due).setText("Due : ${duein} days")
        } else {
            holder.itemView.findViewById<TextView>(R.id.due).setText("Due Today")
        }

        holder.itemView.findViewById<TextView>(R.id.deletetodolistbutton).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                viewModel.removeToDoList(todolistid.toString())
                withContext(Dispatchers.Main) {
                    viewModel.getToDoList()
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun updateUserList(newToDolist: MutableList<GetToDoListTasks_Details>) {
        todolistList.clear()
        todolistList = newToDolist
        notifyDataSetChanged()
    }


}