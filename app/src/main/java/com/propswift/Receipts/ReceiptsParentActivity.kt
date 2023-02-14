package com.propswift.Receipts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propswift.Shared.settingsClick
import com.propswift.databinding.ActivityReceiptsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReceiptsParentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {

        settingsClick(binding.include.menuicon)
        binding.include.header.setText("Receipts")

        val mViewPager = binding.tabPager
        val mPagerAdapter = ReceiptsAdapter(supportFragmentManager)
        val mtabLayout = binding.include.mainTabs

        mViewPager.adapter = mPagerAdapter
//        mViewPager.offscreenPageLimit = 2
        mtabLayout.setupWithViewPager(mViewPager)

    }
}