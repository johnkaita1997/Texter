package com.propswift.Property

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.Typed2EpoxyController
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.databinding.ExpenserecyclerviewBinding
import kotlinx.coroutines.*

//EXPENSE ACTIVITY//
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.expenserecyclerview)
abstract class ShowImagesModalClass(
    var activity: Activity, var item: Uri,
) : EpoxyModelWithHolder<ShowImagesModalClass.ViewHolder>() {

    private lateinit var binding: ExpenserecyclerviewBinding

    override fun bind(holder: ViewHolder) {
        binding.imageView.setImageURI(
            item
        )
    }

    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {
            binding = ExpenserecyclerviewBinding.bind(itemView)
        }
    }
}

class PhotoController : Typed2EpoxyController<List<Uri>, Activity>() {
    override fun buildModels(photos: List<Uri>, activity: Activity) {
        photos.forEachIndexed { index, uri ->
            ShowImagesModalClass_(activity, uri).id(uri.toString()).addTo(this)
        }
    }
}