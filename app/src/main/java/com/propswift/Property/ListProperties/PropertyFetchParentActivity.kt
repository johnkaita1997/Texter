package com.propswift.Property.ListProperties

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propswift.Shared.settingsClick
import com.propswift.databinding.ActivityPropertyBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PropertyFetchParentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPropertyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        settingsClick(binding.include.menuicon)

        binding.include.header.setText("Listed Properties")

        val mViewPager = binding.tabPager
        val mPagerAdapter = PropertyFetchAdapter(supportFragmentManager)
        val mtabLayout = binding.include.mainTabs

        mViewPager.adapter = mPagerAdapter
        mViewPager.offscreenPageLimit = 2

        mtabLayout.setupWithViewPager(mViewPager)

    }


}