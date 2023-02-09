package com.propswift.Receipts

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.propswift.Receipts.View.OtherReceipts.OtherReceiptsFragment
import com.propswift.Receipts.View.RentReceipts.RentReceiptsFragment


class ReceiptsAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                RentReceiptsFragment()
            }
            1 -> {
                OtherReceiptsFragment()
            }
            else -> RentReceiptsFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Rent Receipts"
            1 -> "Other Receipts"
            else -> null
        }
    }
}