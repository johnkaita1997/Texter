package com.propswift.Property.ListProperties.Rented

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Receipts.Add.RentReceipt.ListPendingRent
import com.propswift.Receipts.ReceiptsParentActivity
import com.propswift.Shared.*
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.coroutines.*

class RentedPropertyAdapter(var activity: FragmentActivity, var rentedPropertyList: MutableList<RentedDetail>?, var viewmodel:MyViewModel) : RecyclerView.Adapter<RentedPropertyAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.view_fragmentowned, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rentedPropertyList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rentObject = rentedPropertyList!!.get(position);

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
            CoroutineScope(Dispatchers.IO).launch() {
                viewmodel.removeProperty(propertyId.toString(), "rented")
            }
        }

        holder.itemView.findViewById<Button>(R.id.rent).setOnClickListener {

            val powerMenu: PowerMenu.Builder? = PowerMenu.Builder(activity)
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .addItem(PowerMenuItem("View Rent"))
                .addItem(PowerMenuItem("Pay Rent"))
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
                .setWidth(900).setTextColor(ContextCompat.getColor(activity, R.color.black))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                .setAutoDismiss(true)
                .setOnMenuItemClickListener { position, item ->
                    if (position == 0) {
                        activity.goToactivityIntent_Unfinished(activity, ReceiptsParentActivity::class.java, mutableMapOf("propertyid" to propertyId.toString()))
                    } else {
                        activity.goToactivityIntent_Unfinished(activity, ListPendingRent::class.java, mutableMapOf("propertyid" to propertyId.toString()))
                    }
                }
                powerMenu?.build()?.showAsDropDown(holder.itemView.findViewById<Button>(R.id.rent))


        }



    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun updateRentedProperties(newRentedList: MutableList<RentedDetail>?) {
        rentedPropertyList?.clear()
        rentedPropertyList = newRentedList!!
        notifyDataSetChanged()
    }

    fun filterRentedProperties(stringObject: String) {
        val newlist = mutableListOf<RentedDetail>()
        newlist.clear()
        rentedPropertyList!!.forEach {
            if (it.name?.lowercase()?.contains(stringObject.lowercase()) == true) {
                newlist.add(it)
            }
        }
        rentedPropertyList!!.clear()
        rentedPropertyList = newlist
        notifyDataSetChanged()
    }


}