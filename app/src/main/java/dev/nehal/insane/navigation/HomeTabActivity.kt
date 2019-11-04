package dev.nehal.insane.navigation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.nehal.insane.modules.MainActivity
import kotlinx.android.synthetic.main.activity_tab_home.*




class HomeTabActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dev.nehal.insane.R.layout.activity_tab_home)
        val adapter = HomeViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(GridFragment(), "All")
        adapter.addFragment(DetailViewFragment(), "List")
        adapter.addFragment(AlarmFragment(), "Noti")

        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        // Bottom Navigation View
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
           dev.nehal.insane.R.id.action_home -> {
               val intent = Intent(this,MainActivity::class.java)
               intent.putExtra("id",0)
                startActivity(intent)
                finish()
                return true
            }
           dev.nehal.insane.R.id.action_search -> {
               val intent = Intent(this,MainActivity::class.java)
               intent.putExtra("id",1)
               startActivity(intent)
               finish()
               return true

            }
            dev.nehal.insane.R.id.action_add_photo -> {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("id",2)
                startActivity(intent)
                finish()
                return true
            }
           dev.nehal.insane.R.id.action_favorite_alarm -> {
               val intent = Intent(this,MainActivity::class.java)
               intent.putExtra("id",3)
               startActivity(intent)
               finish()
               return true
            }
            dev.nehal.insane.R.id.action_account -> {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("id","3")
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }
}