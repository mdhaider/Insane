package dev.nehal.insane.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.nehal.insane.R
import kotlinx.android.synthetic.main.activity_tab.*

class TabActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(AlarmFragment(), "Activity")
        adapter.addFragment(PeopleFragment(), "People")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
}