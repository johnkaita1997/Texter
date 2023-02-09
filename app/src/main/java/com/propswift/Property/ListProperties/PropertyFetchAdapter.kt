package com.propswift.Property.ListProperties

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.propswift.Property.ListProperties.Owned.OwnedPropertyFragment


class PropertyFetchAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                RentedPropertyFragment()
            }
            1 -> {
                OwnedPropertyFragment()
            }
            else -> RentedPropertyFragment()
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