package com.propswift.Receipts.View.OtherReceipts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.ImageViewer.ImageViewActivity
import com.propswift.R
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.OtherReceiptCallbackDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class OtherReceiptsAdapter(var activity: FragmentActivity, var expenseList: MutableList<OtherReceiptCallbackDetails>?, var viewmodel: MyViewModel) :
    RecyclerView.Adapter<OtherReceiptsAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_otherreceipts, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expenseList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val expenseObject = expenseList!!.get(position);
        val expenseDate = expenseObject.date_incurred
        val expenseAmount = expenseObject.amount
        val propertyName = expenseObject.property
        val description = expenseObject.description
        val files = expenseObject.files

        holder.itemView.findViewById<TextView>(R.id.display_receipt_amount).setText("KES : ${expenseAmount}")
        holder.itemView.findViewById<TextView>(R.id.display_receipt_valutiondate).setText(expenseDate)
        holder.itemView.findViewById<TextView>(R.id.display_other_receipt_propertyName).setText(propertyName)
        holder.itemView.findViewById<TextView>(R.id.display_description).setText(description)


        holder.itemView.findViewById<Button>(R.id.viewimages).setOnClickListener {
            val imagesList = files
            val intent = Intent(activity, ImageViewActivity::class.java)
            intent.putStringArrayListExtra("imageslist", imagesList as ArrayList<String?>?)
            activity.startActivity(intent)
        }


        holder.itemView.findViewById<Button>(R.id.delete).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch() {
                viewmodel.deleteOtherReceipt(expenseObject.id)
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun updateExpenseAdapter(newexpenseList: MutableList<OtherReceiptCallbackDetails>?) {
        expenseList?.clear()
        expenseList = newexpenseList
        notifyDataSetChanged()
    }

}