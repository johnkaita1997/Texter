package com.tafatalkstudent.Activities

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.*
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale


class ViewGroupsAdapter(var viewModel: MyViewModel, var activity: Activity, var groups: List<Groups>) : RecyclerView.Adapter<ViewGroupsAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.displaygroups, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val group = groups[position]
        holder.groupName.text = group.name.toString()

        threadScope.launch {
            val latestGroupMessage = async { viewModel.getLatestGroupMessage(group.id!!, activity) }.await()
            latestGroupMessage?.body?.let {
                mainScope.launch {
                    holder.groupMessage.text = latestGroupMessage.body.toString()
                    if (latestGroupMessage.isRead == false) {
                        holder.groupName.setTypeface(null, Typeface.BOLD);
                        holder.groupMessage.setTypeface(null, Typeface.BOLD);
                    } else {
                        holder.groupName.setTypeface(null, Typeface.NORMAL);
                        holder.groupMessage.setTypeface(null, Typeface.NORMAL);
                    }
                }
            }

        }

        holder.itemView.setOnClickListener {
            activity.goToactivityIntent_Unfinished(
                activity, GroupSmsActivity::class.java, mutableMapOf(
                    "groupId" to group.id.toString(),
                    "groupName" to group.name.toString(),
                    "groupDescription" to group.description.toString(),
                )
            )
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.groupNameTv)
        val groupMessage: TextView = itemView.findViewById(R.id.groupMessage)
    }

    fun setData(newGroups: MutableList<Groups>) {
        groups = newGroups
        notifyDataSetChanged()
    }

}