package dev.nehal.insane.newd.main.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dev.nehal.insane.R
import dev.nehal.insane.navigation.AlarmFragment
import dev.nehal.insane.navigation.DetailViewFragment
import dev.nehal.insane.navigation.GridFragment
import dev.nehal.insane.newd.main.ui.dashboard.ui.main.SectionsPagerAdapter

class TabActivity1 : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_tab1, container, false)
        viewPager = root.findViewById(R.id.view_pager)
        tabs = root.findViewById(R.id.tabs)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SectionsPagerAdapter(activity!!, childFragmentManager)
        adapter.addFragment(GridFragment(), "All")
        adapter.addFragment(DetailViewFragment(), "List")
        adapter.addFragment(AlarmFragment(), "Noti")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

    }
}

