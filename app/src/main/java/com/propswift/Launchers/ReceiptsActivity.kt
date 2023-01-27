package com.propswift.Launchers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propswift.Shared.ReceiptsAdapter
import com.propswift.databinding.ActivityReceiptsBinding

class ReceiptsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    private fun initall() {
        binding.include.header.setText("Receipts")

        val mViewPager = binding.tabPager
        val mPagerAdapter = ReceiptsAdapter(supportFragmentManager)
        val mtabLayout = binding.include.mainTabs

        mViewPager.adapter = mPagerAdapter
        mViewPager.offscreenPageLimit = 2
        mtabLayout.setupWithViewPager(mViewPager)
    }
}