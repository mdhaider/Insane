package dev.nehal.insane.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dev.nehal.insane.postlogin.DetailFragment

class HomeTabFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(dev.nehal.insane.R.layout.fragment_home_tab, container, false)
        viewPager = root.findViewById(dev.nehal.insane.R.id.view_pager)
        tabs = root.findViewById(dev.nehal.insane.R.id.tabs)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter =
            SectionsPagerAdapter(
                activity!!,
                childFragmentManager
            )
        adapter.addFragment(GridFragment(), "")
        adapter.addFragment(DetailFragment(), "")
        adapter.addFragment(AlarmFragment(), "")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        setTab()

    }

    private fun setTab() {
        val tabIcons = intArrayOf(dev.nehal.insane.R.drawable.ic_tab_2, dev.nehal.insane.R.drawable.ic_1_tab, dev.nehal.insane.R.drawable.ic_favorite_border_black_24dp)
        for (i in 0 until tabs.tabCount) {
            if (tabs.getTabAt(i) != null) {
                tabs.getTabAt(i)!!.setIcon(tabIcons[i])
            }
        }
    }
}

