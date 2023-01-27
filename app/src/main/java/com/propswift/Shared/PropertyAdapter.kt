package com.propswift.Shared

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.propswift.Activities.OwnedFragment
import com.propswift.Activities.RentedFragment


class PropertyAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                RentedFragment()
            }
            1 -> {
                OwnedFragment()
            }
            else -> RentedFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Rented"
            1 -> "Owned"
            else -> null
        }
    }
}