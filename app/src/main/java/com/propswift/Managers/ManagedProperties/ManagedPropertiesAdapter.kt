package com.propswift.Managers.ManagedProperties

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.Expenses.ViewExpensesActivity
import com.propswift.R
import com.propswift.Receipts.Add.RentReceipt.ListPendingRent
import com.propswift.Receipts.ReceiptsParentActivity
import com.propswift.Shared.ListManagedPropertiesDetail
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.goToactivityIntent_Unfinished
import com.propswift.Shared.showAlertDialog
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManagedPropertiesAdapter(var activity: FragmentActivity, var managedPropertyList: MutableList<ListManagedPropertiesDetail>, var viewmodel: MyViewModel) :
    RecyclerView.Adapter<ManagedPropertiesAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.view_fragmentmanagedproperties, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return managedPropertyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rentObject = managedPropertyList.get(position);

        holder.itemView.findViewById<TextView>(R.id.propertyname).setText("${rentObject.name}")
        holder.itemView.findViewById<TextView>(R.id.propertylocation).setText("${rentObject.location}")
        holder.itemView.findViewById<TextView>(R.id.areaTv).setText("${rentObject.area} + ${rentObject.area_unit}")

        holder.itemView.findViewById<TextView>(R.id.totalexpenses)
        holder.itemView.findViewById<TextView>(R.id.numberofreceipts)

        val imageview = holder.itemView.findViewById<ImageView>(R.id.picture)
        if (rentObject.files.isNotEmpty()) {
            val image = rentObject.files.get(0)
            Picasso.get().load(image.toString())
                .fit()
                .into(imageview)

            activity.showAlertDialog(image.toString())
        }

        CoroutineScope(Dispatchers.IO).launch() {
            viewmodel.getTotalExpenseOnProperty(rentObject.id, holder.itemView.findViewById<TextView>(R.id.totalexpenses))
        }

        CoroutineScope(Dispatchers.IO).launch() {
            viewmodel.getTotalNumberofReceiptsPerHouse(rentObject.id, holder.itemView.findViewById<TextView>(R.id.numberofreceipts))
        }

        holder.itemView.findViewById<TextView>(R.id.rentTv).setText("KES ${rentObject.rent_amount.toString()}")

        val propertyId = rentObject.id

        holder.itemView.findViewById<CardView>(R.id.expensescard).setOnClickListener {
            activity.goToactivityIntent_Unfinished(activity, ViewExpensesActivity::class.java, mutableMapOf("propertyid" to propertyId.toString()))
        }

        holder.itemView.findViewById<CardView>(R.id.receiptcard).setOnClickListener {
            activity.goToactivityIntent_Unfinished(activity, ReceiptsParentActivity::class.java, mutableMapOf("propertyid" to propertyId.toString()))
        }

        holder.itemView.findViewById<Button>(R.id.removeBtn).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                viewmodel.removeProperty(propertyId.toString(), "owned")
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

    fun updateManagedProperties(newOwnedPropertyList: MutableList<ListManagedPropertiesDetail>?) {
        managedPropertyList.clear()
        managedPropertyList = newOwnedPropertyList!!
        notifyDataSetChanged()
    }

    fun filterOwnedProperties(stringObject: String) {
        val newlist = mutableListOf<ListManagedPropertiesDetail>()
        newlist.clear()
        managedPropertyList.forEach {
            if (it.name?.lowercase()?.contains(stringObject.lowercase()) == true) {
                newlist.add(it)
            }
        }
        managedPropertyList.clear()
        managedPropertyList = newlist
        notifyDataSetChanged()
    }


}