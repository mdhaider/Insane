package dev.nehal.insane.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dev.nehal.insane.BuildConfig
import dev.nehal.insane.R
import dev.nehal.insane.postlogin.DetailFragment

class HomeTabFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var btnInvite: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home_tab, container, false)
        viewPager = root.findViewById(R.id.view_pager)
        tabs = root.findViewById(R.id.tabs)
        btnInvite= root.findViewById(R.id.inviteGuest)

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

        btnInvite.setOnClickListener{
         shareApp()
        }

    }


    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insane")
            var shareMessage = "\nWe can’t wait to celebrate with you! Download the Insane app to share your photos with us and everyone at the wedding. Kindly share with family members only.\n\n"
            shareMessage =
                shareMessage+"Click here to download app:\n"+ "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share Via"))
        } catch (e: java.lang.Exception) { //e.toString();
        }
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

