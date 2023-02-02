package com.propswift.Activities

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.RentDetail

class ImagesAdapter(var activity: FragmentActivity, var rentList: List<Uri>?) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.display_images, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rentList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rentObject = rentList!!.get(position);
        val imageview = holder.itemView.findViewById<ImageView>(R.id.displayImage)
        imageview.setImageURI(rentObject)

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}