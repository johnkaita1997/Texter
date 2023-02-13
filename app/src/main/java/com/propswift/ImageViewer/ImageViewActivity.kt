package com.propswift.ImageViewer

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.makeLongToast
import com.propswift.databinding.ActivityImageViewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ImageViewActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityImageViewBinding
    private lateinit var imagelist: MutableList<String>
    private val viewmodel: MyViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initall()
    }

    private fun initall() {

        val imagelist = intent.getStringArrayListExtra("imageslist")

        Log.d("-------", "initall: Found images to be ${imagelist}")
        if (imagelist!!.isEmpty()) {
            makeLongToast("Item has no images")
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        val imagesAdapter = ImagesAdapter(this, imagelist, viewmodel)
        binding.managersRecyclerView.setLayoutManager(layoutManager)
        binding.managersRecyclerView.setLayoutManager(layoutManager)
        binding.managersRecyclerView.setAdapter(imagesAdapter)

    }
}