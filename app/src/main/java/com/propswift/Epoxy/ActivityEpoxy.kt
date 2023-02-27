package com.propswift.Epoxy

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.*
import com.airbnb.epoxy.preload.Preloadable
import com.propswift.DisplayImageBindingModel_
import com.propswift.HeaderBindingModel_
import com.propswift.LoaderBindingModel_
import com.propswift.R
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

        class PhotoController : Typed2EpoxyController<List<Photo>, Boolean>(), Preloadable {
            override fun buildModels(photos: List<Photo>, loadingMore: Boolean) {
                HeaderBindingModel_()
                    .id(0)
                    .description("A description")
                    .header("A header")
                    .title("A title")
                    .addTo(this)

                val horizontalPhotos = photos.map { photo ->
                    DisplayImageBindingModel_()
                        .id(photo.name)
                        .name(photo.name)
                        .click { view ->
                            Toast.makeText(view.context, "Image clicked!", Toast.LENGTH_SHORT).show()
                        }
                        .image("https://www.shutterstock.com/image-photo/surreal-image-african-elephant-wearing-260nw-1365289022.jpg")
                }

                CarouselModel_()
                    .id("carousel")
                    .numViewsToShowOnScreen(2F)
                    .models(horizontalPhotos)
                    .addTo(this)

                photos.forEach { photo ->
                    DisplayImageBindingModelNew_()
                        .id(photo.name)
                        .name(photo.name)
                        .image("https://www.shutterstock.com/image-photo/surreal-image-african-elephant-wearing-260nw-1365289022.jpg")
                        .addTo(this)
                }

                LoaderBindingModel_()
                    .id("loader")
                    .addIf(loadingMore, this)
            }

            override val viewsToPreload: List<View>
                get() = emptyList()

        }


        val controller = PhotoController()
        binding.epoxyRecyclerView.setAdapter(controller.getAdapter())
        controller.setData(listOf(), true)
        controller.setData(
            listOf(Photo("New Photo", "Image"), Photo("New Photo", "Image"), Photo("New Photo", "Image"), Photo("New Photo", "Image"), Photo("New Photo", "Image")), true
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


@EpoxyModelClass(layout = R.layout.epox_display_image)
abstract class DisplayImageBindingModelNew : EpoxyModelWithHolder<DisplayImageBindingModelNew.ViewHolder>() {

    @EpoxyAttribute
    lateinit var name: String

    @EpoxyAttribute
    lateinit var image: String
    private lateinit var binding: EpoxDisplayImageBinding

    override fun bind(holder: ViewHolder) {
        binding.name = name
        Picasso.get().load(image).into(binding.displayImage)
        binding.named.setOnClickListener {
            Toast.makeText(it.context, "Name finally clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {
            binding = EpoxDisplayImageBinding.bind(itemView)
        }

    }

}


