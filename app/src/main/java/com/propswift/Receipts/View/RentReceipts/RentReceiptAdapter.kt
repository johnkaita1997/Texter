package com.propswift.Receipts.View.RentReceipts

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
import com.propswift.Shared.RentDetail

class RentReceiptAdapter(var activity: FragmentActivity, var rentalsList: MutableList<RentDetail>?) : RecyclerView.Adapter<RentReceiptAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_rentreceipts, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rentalsList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rentObject = rentalsList!!.get(position);
        val rentDate = rentObject.date_paid
        val rentAmount = rentObject.amount
//        val receiptNumber = rentObject.
        val propertyName = rentObject.property.name
        val rentStatus = rentObject.rent_status

        holder.itemView.findViewById<TextView>(R.id.display_rental_amount).setText("KES : ${rentAmount}")
        holder.itemView.findViewById<TextView>(R.id.display_rrental_datePaid).setText(rentDate)
        holder.itemView.findViewById<TextView>(R.id.display_rental_propertyName).setText(propertyName)
        holder.itemView.findViewById<TextView>(R.id.display_rental_status).setText(rentStatus)

        holder.itemView.findViewById<Button>(R.id.images).setOnClickListener {
            val imagesList = rentObject.payment_files
            val intent = Intent(activity, ImageViewActivity::class.java)
            intent.putStringArrayListExtra("imageslist", imagesList as ArrayList<String?>?)
            activity.startActivity(intent)
        }

        /*holder.itemView.findViewById<Button>(R.id.customwatchvideo).setOnClickListener {
            val videoid = rentObject.videos.get(0).videoid
            activity.playVideos(videoid, topicName)
        }*/

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun updateRentalsAdapter(newRentalsList: MutableList<RentDetail>?) {
        rentalsList?.clear()
        rentalsList = newRentalsList
        notifyDataSetChanged()
    }

}