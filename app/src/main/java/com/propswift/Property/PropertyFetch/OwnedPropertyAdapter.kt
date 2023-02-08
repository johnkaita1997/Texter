package com.propswift.Property.PropertyFetch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Receipts.ReceiptsParentActivity
import com.propswift.Shared.OwnedDetail
import com.propswift.Shared.RentDetail
import com.propswift.Shared.RentedDetail
import com.propswift.Shared.goToactivityIntent_Unfinished
import org.apache.commons.lang3.mutable.Mutable

class OwnedPropertyAdapter(var activity: FragmentActivity, var ownedPropertiesList: MutableList<OwnedDetail>) : RecyclerView.Adapter<OwnedPropertyAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.view_fragmentowned, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ownedPropertiesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rentObject = ownedPropertiesList.get(position);

        holder.itemView.findViewById<TextView>(R.id.propertyname).setText("${rentObject.name}")
        holder.itemView.findViewById<TextView>(R.id.propertylocation).setText("${rentObject.location}")
        holder.itemView.findViewById<TextView>(R.id.areaTv).setText("${rentObject.area} + ${rentObject.area_unit}")
        holder.itemView.findViewById<TextView>(R.id.rentTv).setText("KES ${rentObject.rent_amount.toString()}")

        /*holder.itemView.findViewById<Button>(R.id.customwatchvideo).setOnClickListener {
            val videoid = rentObject.videos.get(0).videoid
            activity.playVideos(videoid, topicName)
        }*/

        val propertyId = rentObject.id

        holder.itemView.findViewById<CardView>(R.id.expensescard).setOnClickListener {
            activity.goToactivityIntent_Unfinished(activity, ReceiptsParentActivity::class.java, mutableMapOf("propertyid" to propertyId.toString()))
        }

        holder.itemView.findViewById<CardView>(R.id.receiptcard).setOnClickListener {
            activity.goToactivityIntent_Unfinished(activity, ReceiptsParentActivity::class.java, mutableMapOf("propertyid" to propertyId.toString()))
        }

        holder.itemView.findViewById<Button>(R.id.removeBtn).setOnClickListener {
            activity.goToactivityIntent_Unfinished(activity, ReceiptsParentActivity::class.java, mutableMapOf("propertyid" to propertyId.toString()))
        }


    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun updateOwnedProperties(newOwnedPropertyList: MutableList<OwnedDetail>?) {
        ownedPropertiesList.clear()
        ownedPropertiesList = newOwnedPropertyList!!
        notifyDataSetChanged()
    }

    fun filterOwnedProperties(stringObject : String) {
        val newlist = mutableListOf<OwnedDetail>()
        newlist.clear()
        ownedPropertiesList.forEach {
            if (it.name?.lowercase()?.contains(stringObject.lowercase()) == true) {
                newlist.add(it)
            }
        }
        ownedPropertiesList.clear()
        ownedPropertiesList = newlist
        notifyDataSetChanged()
    }



}