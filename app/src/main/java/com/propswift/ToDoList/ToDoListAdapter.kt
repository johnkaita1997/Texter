package com.propswift.ToDoList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.GetToDoListTasks_Details
import com.propswift.Shared.makeLongToast
import com.propswift.Shared.myViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToDoListAdapter(var activity: FragmentActivity, var todolistList: MutableList<GetToDoListTasks_Details>?) : RecyclerView.Adapter<ToDoListAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_todolist, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todolistList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val todoListObject = todolistList!!.get(position);
        val todolistid = todoListObject.id
        val todolistname    = todoListObject.title

        holder.itemView.findViewById<TextView>(R.id.todolisttext).setText(todolistname)

        holder.itemView.findViewById<TextView>(R.id.deletetodolistbutton).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                activity.myViewModel(activity).removeToDoList(todolistid.toString())
                withContext(Dispatchers.Main) {
                    activity.makeLongToast("To do list item was removed successfully")
                }
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}