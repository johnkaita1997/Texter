package com.propswift.Launchers

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.epoxy.*
import com.propswift.Launchers.*
import com.propswift.R
import com.propswift.Retrofit.Login.*
import com.propswift.Shared.*
import com.propswift.databinding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OwnedFragment : Fragment() {

    private lateinit var viewy: View
    private var _binding: FragmentOwnedBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O) override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOwnedBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        CoroutineScope(Dispatchers.IO).launch() {
            val listOfOwnedProperties = activity?.myViewModel(requireActivity())?.getOwnedproperties()
            withContext(Dispatchers.Main) {

                if (listOfOwnedProperties?.details!!.isEmpty()) {
                    return@withContext
                }


                binding.epoxyRecyclerview.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
                    override fun buildModels(controller: EpoxyController) {
                        controller.apply {
                            listOfOwnedProperties.details.forEachIndexed { index, item ->
                                OwnedModalClass_(activity, item).id(index).addTo(this@apply)
                            }
                        }
                    }

                })
            }
        }

    }

}


@SuppressLint("NonConstantResourceId") @EpoxyModelClass(layout = R.layout.view_fragmentowned) abstract class OwnedModalClass(var activity: FragmentActivity?, var item: OwnedDetail) : EpoxyModelWithHolder<OwnedModalClass.ViewHolder>() {

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
