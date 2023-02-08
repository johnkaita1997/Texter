package com.propswift.Property.PropertyFetch

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.marwaeltayeb.progressdialog.ProgressDialog
import com.propswift.Shared.*
import com.propswift.databinding.FragmentOwnedBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class OwnedPropertyFragment : Fragment(), LifecycleOwner {

    private lateinit var viewy: View
    private var _binding: FragmentOwnedBinding? = null
    private val binding get() = _binding!!
    private lateinit var ownedProgress: ProgressDialog

    private val viewmodel: MyViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOwnedBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        val layoutManager = LinearLayoutManager(activity)
        lateinit var ownedPropertyAdapter: OwnedPropertyAdapter
        ownedPropertyAdapter = OwnedPropertyAdapter(requireActivity(), mutableListOf())
        binding.epoxyRecyclerview.setLayoutManager(layoutManager)
        binding.epoxyRecyclerview.setAdapter(ownedPropertyAdapter)

        binding.filter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 0) {
                    ownedPropertyAdapter.filterOwnedProperties(s.toString())
                    Log.d("-------", "initall: CHECKING FOR ${s}")
                } else {
                    CoroutineScope(Dispatchers.IO).launch() {
                        viewmodel.getOwnedproperties()
                    }
                }
            }
        })

        viewmodel.listOfOwnedProperties.observe(viewLifecycleOwner, Observer {
            ownedPropertyAdapter.updateOwnedProperties(it)
        })

        CoroutineScope(Dispatchers.IO).launch() {
            viewmodel.getOwnedproperties()
        }


    }

}



