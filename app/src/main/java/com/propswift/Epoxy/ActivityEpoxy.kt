package com.propswift.Epoxy

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.*
import com.airbnb.epoxy.EpoxyAttribute.*
import com.airbnb.epoxy.EpoxyAttribute.Option.*
import com.airbnb.epoxy.preload.Preloadable
import com.propswift.*
import com.propswift.Shared.Photo
import com.propswift.databinding.ActivityEpoxy2Binding
import com.propswift.databinding.EpoxDisplayImageBinding
import com.squareup.picasso.Picasso


class ActivityEpoxy : AppCompatActivity() {

    private lateinit var binding: ActivityEpoxy2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEpoxy2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initall()

    }


    private fun initall() {

        val controller = PhotoController()
        binding.epoxyRecyclerView.setAdapter(controller.getAdapter())
        controller.setData(listOf(), true)
        controller.setData(
            listOf(
                Photo("New Photo", "Image"),
                Photo("New Photo", "Image"),
                Photo("New Photo", "Image"),
                Photo("New Photo", "Image"),
                Photo("New Photo", "Image"),
            ), true
        )


        binding.mybutton.setOnClickListener {
            controller.setData(
                listOf(
                    Photo("New Photo", "Image"),
                ), false
            )
        }

    }


}


class PhotoController : Typed2EpoxyController<List<Photo>, Boolean>(), Preloadable {

    override fun buildModels(photos: List<Photo>, loadingMore: Boolean) {

        HeaderBindingModel_()
            .id(0)
            .description("A description")
            .header("A header")
            .title("A title")
            .click(OnClickListener {
                Log.d("-------", "initall: 111111111")
            })
            .addTo(this)

        val horizontalPhotos = mutableListOf<DisplayImageBindingModel_>()
        photos.forEachIndexed { index, photo ->
            horizontalPhotos.add(
                DisplayImageBindingModel_()
                    .id(photo.name)
                    .name(photo.name)
                    .image("https://www.shutterstock.com/image-photo/surreal-image-african-elephant-wearing-260nw-1365289022.jpg")
            )
        }

        CarouselModel_()
            .id("carousel")
            .numViewsToShowOnScreen(2F)
            .models(horizontalPhotos)
            .addTo(this)


        photos.forEachIndexed { index, photo ->
            DisplayImageBindingModelNew_()
                .id(photo.name)
                .name(photo.name)
                .image("https://www.shutterstock.com/image-photo/surreal-image-african-elephant-wearing-260nw-1365289022.jpg")
                .click(OnClickListener {
                    Log.d("-------", "initall: 333333")
                })
                .addTo(this)
        }


        if (loadingMore) LoaderBindingModel_()
            .id("loader")
            .addIf(loadingMore, this)

    }

    override val viewsToPreload: List<View>
        get() = TODO("Not yet implemented")

}


@EpoxyModelClass(layout = R.layout.epox_display_image)
abstract class DisplayImageBindingModelNew : DataBindingEpoxyModel() {

    @EpoxyAttribute
    var name: String? = null

    @EpoxyAttribute
    var image: String? = null

    @EpoxyAttribute
    var click: OnClickListener? = null


    override fun setDataBindingVariables(binding: ViewDataBinding) {
        if (binding is EpoxDisplayImageBinding) {
            binding.name = name
            val imageView = binding.displayImage
            Picasso.get()
                .load(image)
                .into(imageView)
            binding.setVariable(2, click)
        }
    }

}
