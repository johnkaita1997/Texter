package com.propswift.Receipts.View.OtherReceipts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.OtherReceiptCallbackDetails

class OtherReceiptsAdapter(var activity: FragmentActivity, var expenseList: MutableList<OtherReceiptCallbackDetails>?) : RecyclerView.Adapter<OtherReceiptsAdapter.ViewHolder>() {

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
//        val propertyName = expenseObject.property.name

        holder.itemView.findViewById<TextView>(R.id.display_receipt_amount).setText("KES : ${expenseAmount}")
        holder.itemView.findViewById<TextView>(R.id.display_receipt_valutiondate).setText(expenseDate)
        holder.itemView.findViewById<TextView>(R.id.display_receipt_propertyName).setText("")

        /*holder.itemView.findViewById<Button>(R.id.customwatchvideo).setOnClickListener {
            val videoid = expenseObject.videos.get(0).videoid
            activity.playVideos(videoid, topicName)
        }*/

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun updateExpenseAdapter(newexpenseList: MutableList<OtherReceiptCallbackDetails>?) {
        expenseList?.clear()
        expenseList = newexpenseList
        notifyDataSetChanged()
    }

}