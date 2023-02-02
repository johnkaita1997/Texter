package com.propswift.Property.PropertyFetch

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.marwaeltayeb.progressdialog.ProgressDialog
import com.propswift.Shared.*
import com.propswift.databinding.FragmentOwnedBinding
import kotlinx.coroutines.*

class OwnedPropertyFragment : Fragment() {

    private lateinit var viewy: View
    private var _binding: FragmentOwnedBinding? = null
    private val binding get() = _binding!!
    private lateinit var ownedProgress: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOwnedBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        val layoutManager = LinearLayoutManager(activity)
        lateinit var expensesAdapter: OwnedPropertyAdapter
        binding.epoxyRecyclerview.setLayoutManager(layoutManager)

        CoroutineScope(Dispatchers.IO).launch() {
           val listOfOwnedProperties = async { activity?.myViewModel(requireActivity())?.getOwnedproperties() }
            withContext(Dispatchers.Main) {
                listOfOwnedProperties!!.await()?.details.let {
                    if (it!!.isNotEmpty()) {
                        expensesAdapter = OwnedPropertyAdapter(requireActivity(), it)
                        binding.epoxyRecyclerview.setAdapter(expensesAdapter)
                        expensesAdapter.notifyDataSetChanged()
                        activity?.dismissProgress()
                    }
                }
            }
        }


    }

}



