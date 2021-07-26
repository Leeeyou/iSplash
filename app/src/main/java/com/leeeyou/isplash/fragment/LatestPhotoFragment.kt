package com.leeeyou.isplash.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.leeeyou.isplash.R
import com.leeeyou.isplash.Tools
import com.leeeyou.isplash.adapter.LatestPhotoAdapter
import com.leeeyou.isplash.data.PhotoResponse
import com.leeeyou.isplash.ext.observerDefault
import com.leeeyou.isplash.viewmodel.LatestPhotoViewModel
import com.orhanobut.logger.Logger.d
import kotlinx.android.synthetic.main.fragment_lastest_photos.*
import kotlinx.coroutines.launch

internal const val UNSPLASH_STARTING_PAGE_INDEX = 1
internal const val UNSPLASH_PER_PAGE = 10

/**
 * Description: latest photo list page
 *
 * Author:      liyou
 * Date:        2021/7/13 2:23 pm
 */
class LatestPhotosFragment : Fragment(R.layout.fragment_lastest_photos) {

    private val viewModel: LatestPhotoViewModel by viewModels()
    private var pageNo = UNSPLASH_STARTING_PAGE_INDEX
    private var perPage = UNSPLASH_PER_PAGE
    private var firstAutoLoad = true
    private val adapter: LatestPhotoAdapter by lazy {
        LatestPhotoAdapter()
    }
    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.photoList.observerDefault(this,
            onSuccess = {
                setPhotoData(it, adapter)
            },
            onComplete = {
                if (isStartingPage()) refreshLayout.finishRefresh()
                else refreshLayout.finishLoadMore()
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setOnItemClickListener { _, _, position ->
            val bundle = bundleOf("photoId" to adapter.getItem(position).id)
            Navigation.findNavController(view).navigate(R.id.photo_detail_fragment, bundle)
        }

        rv_latest_photo?.adapter = adapter
        if (linearLayoutManager == null) {
            linearLayoutManager = LinearLayoutManager(context)
            rv_latest_photo?.layoutManager = linearLayoutManager
        }

        if (firstAutoLoad) {
            d("firstAutoLoad is $firstAutoLoad")
            refreshLayout?.autoRefresh()
            firstAutoLoad = false
        }

        refreshLayout?.setOnRefreshListener { _ ->
            onRefresh()
        }
        refreshLayout?.setOnLoadMoreListener { _ ->
            onLoadMore()
        }
    }

    private fun setPhotoData(
        result: PhotoResponse?,
        adapter: LatestPhotoAdapter?
    ) {
        if (isStartingPage()) {
            if (!result.isNullOrEmpty()) {
                adapter?.setList(result)
            } else {
                adapter?.setNewInstance(null)
                adapter?.setEmptyView(Tools.getEmptyView(requireContext()))
            }
        } else {
            adapter?.removeEmptyView()
            if (!result.isNullOrEmpty()) {
                adapter?.addData(result)
            } else {
                d("no more data")
            }
        }
    }

    private fun isStartingPage() = pageNo == UNSPLASH_STARTING_PAGE_INDEX

    private fun onLoadMore() {
        ++pageNo
        obtainPhotos()
    }

    private fun onRefresh() {
        pageNo = UNSPLASH_STARTING_PAGE_INDEX
        obtainPhotos()
    }

    private fun obtainPhotos() = viewModel.obtainLatestPhotoList(pageNo, perPage)
}