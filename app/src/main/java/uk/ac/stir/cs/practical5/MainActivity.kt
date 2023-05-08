package uk.ac.stir.cs.practical5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Create toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Create tab layout
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        //Add fragments
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_page1))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_page2))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_page3))

        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        //Create pager and adapter
        val viewPager = findViewById<ViewPager>(R.id.pager)
        val adapter = PagerAdapter(supportFragmentManager,
            tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            //Change fragment according to user selection
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}