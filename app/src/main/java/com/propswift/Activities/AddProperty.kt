package com.propswift.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propswift.databinding.ActivityAddpropertyBinding

class AddProperty : AppCompatActivity() {

    private lateinit var binding: ActivityAddpropertyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddpropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

    }


}