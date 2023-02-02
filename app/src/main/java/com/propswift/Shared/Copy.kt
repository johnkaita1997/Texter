/*
package com.propswift.Property.PropertyFetch

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.epoxy.*
import com.marwaeltayeb.progressdialog.ProgressDialog
import com.propswift.R
import com.propswift.Shared.OwnedDetail
import com.propswift.Shared.myViewModel
import com.propswift.Shared.showAlertDialog
import com.propswift.databinding.FragmentOwnedBinding
import com.propswift.databinding.ViewFragmentownedBinding
import kotlinx.coroutines.*

class OwnedPropertyFragment : Fragment() {

    private lateinit var viewy: View
    private var _binding: FragmentOwnedBinding? = null
    private val binding get() = _binding!!
    private lateinit var ownedProgress: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O) override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOwnedBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        CoroutineScope(Dispatchers.IO).launch() {


            withContext(Dispatchers.Main) {
                ownedProgress = com.marwaeltayeb.progressdialog.ProgressDialog(activity).setDialogPadding(50).setTextSize(18F).setProgressBarColor(R.color.propdarkblue).setText("").setCancelable(true)
                    .setDialogTransparent()
                ownedProgress.show()
            }
            val listOfOwnedProperties = async { activity?.myViewModel(requireActivity())?.getOwnedproperties() }
            val listofRentedProperfdties = async { activity?.myViewModel(requireActivity())?.getrentedproperties() }
            val listOfOwnedPropersdfties = async { activity?.myViewModel(requireActivity())?.getOwnedproperties() }
            val listofRentedPropesasdrties = async { activity?.myViewModel(requireActivity())?.getrentedproperties() }

            */
/*val thelistOfOwnedPropderties = if (listOfOwnedProperties.isCompleted) listOfOwnedProperties.await() else {
                activity?.dismissProgress()
                return@launch
            }*//*


            withContext(Dispatchers.Main) {

                val thelist = withContext(Dispatchers.Default) { listOfOwnedProperties.await() }
                Log.d("-------", "initall: Finished owned")


                if (thelist?.details.isNullOrEmpty()) {
                    println("Empty")
                }

                binding.epoxyRecyclerview.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
                    override fun buildModels(controller: EpoxyController) {
                        controller.apply {
                            thelist?.details?.forEachIndexed { index, item ->
                                OwnedModalClass_(activity, item).id(index).addTo(this@apply)
                            }
                        }
                    }
                })

                ownedProgress.dismiss()
            }
        }

    }

}


@SuppressLint("NonConstantResourceId") @EpoxyModelClass(layout = R.layout.view_fragmentowned) abstract class OwnedModalClass(var activity: FragmentActivity?, var item: OwnedDetail) :
        EpoxyModelWithHolder<OwnedModalClass.ViewHolder>() {

    private lateinit var binding: ViewFragmentownedBinding
    override fun bind(holder: ViewHolder) = Unit

    inner class ViewHolder : EpoxyHolder() {
        @SuppressLint("SetTextI18n") override fun bindView(itemView: View) {
            binding = ViewFragmentownedBinding.bind(itemView)

            binding.propertynameSecond.setText(item.name)
            binding.areaTv.setText("Area   :   ${item.area} ${item.area_unit}")
            binding.rentTv.setText("Rent   :   KES ${item.rent_amount}")

            itemView.setOnClickListener {
                activity?.showAlertDialog(item.toString())
            }

        }
    }

}
*/
