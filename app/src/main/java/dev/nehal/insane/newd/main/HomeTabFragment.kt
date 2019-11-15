package dev.nehal.insane.newd.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dev.nehal.insane.R
import dev.nehal.insane.navigation.AlarmFragment
import dev.nehal.insane.navigation.DetailFragment
import dev.nehal.insane.navigation.GridFragment

class HomeTabFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home_tab, container, false)
        viewPager = root.findViewById(R.id.view_pager)
        tabs = root.findViewById(R.id.tabs)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter =
            SectionsPagerAdapter(activity!!, childFragmentManager)
        adapter.addFragment(GridFragment(), "All")
        adapter.addFragment(DetailFragment(), "List")
        adapter.addFragment(AlarmFragment(), "Noti")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

    }
}
