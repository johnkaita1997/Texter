package com.propswift.ImageViewer

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