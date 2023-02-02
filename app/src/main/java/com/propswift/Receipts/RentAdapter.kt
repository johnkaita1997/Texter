package com.propswift.Receipts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.RentDetail

class RentAdapter(var activity: FragmentActivity, var rentList: List<RentDetail>?) : RecyclerView.Adapter<RentAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_rentals, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rentList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rentObject = rentList!!.get(position);
        val rentDate = rentObject.date_paid
        val rentAmount = rentObject.amount
//        val receiptNumber = rentObject.
        val propertyName = rentObject.property.name
        val rentStatus = rentObject.rent_status

        holder.itemView.findViewById<TextView>(R.id.display_rental_amount).setText("KES : ${rentAmount}")
        holder.itemView.findViewById<TextView>(R.id.display_rrental_datePaid).setText(rentDate)
        holder.itemView.findViewById<TextView>(R.id.display_rental_propertyName).setText(propertyName)
        holder.itemView.findViewById<TextView>(R.id.display_rental_receiptnumber).setText(rentStatus)

        /*holder.itemView.findViewById<Button>(R.id.customwatchvideo).setOnClickListener {
            val videoid = rentObject.videos.get(0).videoid
            activity.playVideos(videoid, topicName)
        }*/

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}