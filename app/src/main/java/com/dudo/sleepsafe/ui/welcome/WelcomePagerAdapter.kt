package com.dudo.sleepsafe.ui.welcome

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class WelcomePagerAdapter(fm: androidx.fragment.app.FragmentManager?) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment = WelcomeFragment.newInstance(position)

    override fun getCount(): Int = 5
}