package com.propswift.Receipts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.FetchExpenseObject_Detail
import com.propswift.Shared.GetToDoListTasks_Details

class ExpensesAdapter(var activity: FragmentActivity, var expenseList: MutableList<FetchExpenseObject_Detail>?) : RecyclerView.Adapter<ExpensesAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_receipt, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expenseList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val expenseObject = expenseList!!.get(position);
        val expenseDate = expenseObject.date_incurred
        val expenseAmount = expenseObject.amount
        val receiptNumber = expenseObject.receipt
        val propertyName = expenseObject.property.name

        holder.itemView.findViewById<TextView>(R.id.display_receipt_amount).setText("KES : ${expenseAmount}")
        holder.itemView.findViewById<TextView>(R.id.display_receipt_valutiondate).setText(expenseDate)
        holder.itemView.findViewById<TextView>(R.id.display_receipt_propertyName).setText(propertyName)
        holder.itemView.findViewById<TextView>(R.id.display_receipt_receiptnumber).setText(receiptNumber)

        /*holder.itemView.findViewById<Button>(R.id.customwatchvideo).setOnClickListener {
            val videoid = expenseObject.videos.get(0).videoid
            activity.playVideos(videoid, topicName)
        }*/

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun updateExpenseAdapter(newexpenseList: MutableList<FetchExpenseObject_Detail>?) {
        expenseList?.clear()
        expenseList = newexpenseList
        notifyDataSetChanged()
    }

}