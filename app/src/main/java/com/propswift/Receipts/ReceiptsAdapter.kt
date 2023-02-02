package com.propswift.Receipts

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ReceiptsAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                RentalsFragment()
            }
            1 -> {
                ExpensesFragment()
            }
            else -> RentalsFragment()
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