package com.propswift.ImageViewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.propswift.R
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.showAlertDialog
import com.squareup.picasso.Picasso

class ImagesAdapter(var activity: FragmentActivity, var imagesList: MutableList<String>, var viewmodel: MyViewModel) :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.imageview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val imageObject = imagesList.get(position);

        val imageview = holder.itemView.findViewById<ImageView>(R.id.picture)
        val image = imageObject
        Picasso.get().load(image).fit().into(imageview)
        activity.showAlertDialog(image)

        imageview.setOnClickListener {

        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}