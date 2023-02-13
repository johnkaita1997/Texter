package com.propswift.Managers.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.GetPropertyManagerDetails_Details
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.makeLongToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagersAdapter(var activity: FragmentActivity, var managerList: MutableList<GetPropertyManagerDetails_Details>?, var propertyid: String, var viewmodel: MyViewModel) : RecyclerView.Adapter<ManagersAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_manager, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return managerList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val managerObject = managerList!!.get(position);
        val managerid = managerObject.user_id

        holder.itemView.findViewById<TextView>(R.id.managername).setText("${managerObject.first_name} ${managerObject.last_name}")

        holder.itemView.findViewById<TextView>(R.id.deleteManager).setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch() {
               viewmodel.removeManager(managerid.toString(), propertyid)
                withContext(Dispatchers.Main) {
                    activity.makeLongToast("Manager removed successfully")
                }

            }

        }


    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}