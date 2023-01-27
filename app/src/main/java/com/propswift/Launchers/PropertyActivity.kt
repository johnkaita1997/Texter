package com.propswift.Launchers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propswift.Shared.PropertyAdapter
import com.propswift.databinding.ActivityPropertyBinding

class PropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPropertyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        binding.include.header.setText("Listed Properties")

        val mViewPager = binding.tabPager
        val mPagerAdapter = PropertyAdapter(supportFragmentManager)
        val mtabLayout = binding.include.mainTabs

        mViewPager.adapter = mPagerAdapter
        mViewPager.offscreenPageLimit = 2
        mtabLayout.setupWithViewPager(mViewPager)

    }


}