package com.propswift.Receipts.Add.RentReceipt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.RentDetail
import com.propswift.Shared.goToactivityIntent_Unfinished

class ListPendingRentalsAdapter(var activity: FragmentActivity, var rentalList: MutableList<RentDetail>?) : RecyclerView.Adapter<ListPendingRentalsAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_pending, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rentalList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val expenseObject = rentalList!!.get(position);
        val duedate = expenseObject.due_date
        val expenseAmount = expenseObject.amount
        val propertyName = expenseObject.property.name
        val status = expenseObject.rent_status
        val propertyid = expenseObject.property.id

        val rentid = expenseObject.id

        holder.itemView.findViewById<TextView>(R.id.display_receipt_amount).setText("KES : ${expenseAmount}")
        holder.itemView.findViewById<TextView>(R.id.display_receipt_valutiondate).setText("Due : ${duedate}")
        holder.itemView.findViewById<TextView>(R.id.display_receipt_propertyName).setText(propertyName)
        holder.itemView.findViewById<TextView>(R.id.status).setText(status)

        holder.itemView.setOnClickListener {
            activity.goToactivityIntent_Unfinished(activity, AddRentReceipt::class.java, mutableMapOf("requestid" to rentid.toString(), "propertyid" to propertyid.toString()))
        }



    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun updateRentalAdapter(newRentalList: MutableList<RentDetail>?) {
        rentalList?.clear()
        rentalList = newRentalList
        notifyDataSetChanged()
    }

}