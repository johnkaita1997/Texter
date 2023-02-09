package com.propswift.Receipts.Add.RentReceipt

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
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
import com.propswift.databinding.AddOtherReceiptHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@AndroidEntryPoint
class AddRentReceipt : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: AddOtherReceiptHomeBinding
    private lateinit var imagesAdapter: ImagesAdapter
    lateinit var requestid: String

    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddOtherReceiptHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()

    }


    private fun initall() {

        binding.include.header.setText("Enter Rent Details")
        settingsClick(binding.include.menuicon)
        requestid = intent.getStringExtra("requestid").toString()

        val themap = mutableListOf<MultipartBody.Part>()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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
                    binding.include.header.setText("Add Expense")
                    AddRentReceiptModalClass_(this@AddRentReceipt, launcher, viewmodel).id(0).addTo(this)
                }

            }
        })


    }

}

