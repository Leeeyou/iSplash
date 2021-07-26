package com.leeeyou.isplash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.leeeyou.isplash.adapter.PAGE_INDEX_LATEST_PHOTOS
import com.leeeyou.isplash.adapter.PAGE_INDEX_PHOTO_COLLECTIONS
import com.leeeyou.isplash.adapter.MainFragmentAdapter

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_main, null)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        viewPager.adapter = MainFragmentAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))

        return view
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            PAGE_INDEX_LATEST_PHOTOS -> getString(R.string.home_title_latest)
            PAGE_INDEX_PHOTO_COLLECTIONS -> getString(R.string.home_title_collections)
            else -> null
        }
    }
}
