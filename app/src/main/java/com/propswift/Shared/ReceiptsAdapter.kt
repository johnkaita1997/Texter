package com.propswift.Shared

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.propswift.Launchers.ExpensesFragment
import com.propswift.Launchers.RentFragment


class ReceiptsAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                RentFragment()
            }
            1 -> {
                ExpensesFragment()
            }
            else -> RentFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Rent"
            1 -> "Expenses"
            else -> null
        }
    }
}