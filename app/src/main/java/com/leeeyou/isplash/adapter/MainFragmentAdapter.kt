package com.leeeyou.isplash.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.leeeyou.isplash.fragment.LatestPhotosFragment
import com.leeeyou.isplash.fragment.CollectionsFragment
import com.orhanobut.logger.Logger.d

const val PAGE_INDEX_LATEST_PHOTOS = 0
const val PAGE_INDEX_PHOTO_COLLECTIONS = 1

class MainFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        PAGE_INDEX_LATEST_PHOTOS to {
            d("tabFragmentsCreators PAGE_INDEX_LATEST_PHOTOS")
            LatestPhotosFragment()
        },
        PAGE_INDEX_PHOTO_COLLECTIONS to {
            d("tabFragmentsCreators PAGE_INDEX_PHOTO_COLLECTIONS")
            CollectionsFragment()
        }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
