package com.rigo.ramos.formslibrary.views


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BottomAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments  = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment){
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }


}