package com.propswift.Property

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.propswift.Activities.ImagesAdapter
import com.propswift.Shared.Constants.expenseImageList
import com.propswift.Shared.Constants.expenseImageUploadList
import com.propswift.Shared.MyViewModel
import com.propswift.Shared.makeLongToast
import com.propswift.Shared.settingsClick
import com.propswift.databinding.PropertyProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@AndroidEntryPoint
class PropertyParentActivity : AppCompatActivity() {

    private lateinit var binding: PropertyProfileBinding
    private lateinit var imagesAdapter: ImagesAdapter

    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PropertyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()

    }


    private fun initall() {

        settingsClick(binding.include.menuicon)

        var themap = mutableListOf<MultipartBody.Part>()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.include.header.setText("Add Expense")
        binding.include.mainTabs.visibility = View.GONE
        binding.imagesRecyclerView.setLayoutManager(layoutManager);

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                expenseImageList.clear()
                val myuri = it.data?.extras
                if (myuri?.keySet()!!.contains("extra.file_path")) {
                    val image = myuri.get("extra.file_path") as Uri
                    expenseImageList.add(image)
                    makeLongToast("It is a single image")
                } else {

                    expenseImageList = myuri.get("extra.multiple_file_path") as MutableList<Uri>
                    imagesAdapter = ImagesAdapter(this, expenseImageList)
                    binding.imagesRecyclerView.setAdapter(imagesAdapter)

                    imagesAdapter.notifyDataSetChanged()

                    expenseImageList.forEach {
                        var file = File(it.path!!)
                        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file[]", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                        themap.add(filePart)
                    }

                }

                CoroutineScope(Dispatchers.IO).launch() {
                    val imagelist = viewmodel.uploadFile(themap)
                    expenseImageUploadList = imagelist
                }

            }
        }


        binding.receiptImages.setOnClickListener {
            ImagePicker.with(this).provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .crop().cropFreeStyle().setMultipleAllowed(true).createIntentFromDialog { launcher.launch(it) }
        }
        val operation = intent.getStringExtra("operation")

        binding.epoxyRecyclerview.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                controller.apply {
                    if (operation == "createproperty") {
                        binding.include.header.setText("Add Property")
                        AddPropertyModalClass_(this@PropertyParentActivity, viewmodel).id(0).addTo(this)
                    } else if (operation == "createexpense") {
                        binding.include.header.setText("Add Expense")
                        AddExpenseModalClass_(this@PropertyParentActivity, launcher, viewmodel).id(0).addTo(this)
                    }

                }

            }
        })


    }

}

