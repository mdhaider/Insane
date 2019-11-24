package dev.nehal.insane.newd.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.getCurrentNavigationFragment(): Fragment? =
    primaryNavigationFragment?.childFragmentManager?.fragments?.first()