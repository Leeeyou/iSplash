package com.leeeyou.isplash.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.leeeyou.isplash.R
import com.leeeyou.isplash.Tools
import com.leeeyou.isplash.adapter.CollectionsAdapter
import com.leeeyou.isplash.data.CollectionResponse
import com.leeeyou.isplash.data.bean.Collection
import com.leeeyou.isplash.ext.observerDefault
import com.leeeyou.isplash.ext.showToast
import com.leeeyou.isplash.viewmodel.CollectionViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_collections.*
import kotlinx.android.synthetic.main.fragment_lastest_photos.refreshLayout

/**
 * Description: collections page
 *
 * Author:      liyou
 * Date:        2021/7/13 5:12 pm
 */
class CollectionsFragment : Fragment(R.layout.fragment_collections) {

    private val viewModel: CollectionViewModel by viewModels()
    private var pageNo = UNSPLASH_STARTING_PAGE_INDEX
    private var perPage = UNSPLASH_PER_PAGE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val collectionsAdapter = CollectionsAdapter()

        viewModel.collections.observerDefault(this,
            onSuccess = {
                setCollectionsData(it, collectionsAdapter)
            },
            onComplete = {
                if (isStartingPage()) refreshLayout.finishRefresh()
                else refreshLayout.finishLoadMore()
            })

        rv_collections?.adapter = collectionsAdapter
        rv_collections?.layoutManager = LinearLayoutManager(context)

        collectionsAdapter.setOnItemClickListener { adapter, _, position ->
            (adapter.getItem(position) as? Collection)?.id?.let { showToast("collection id is $it") }
        }

        refreshLayout?.autoRefresh()
        refreshLayout?.setOnRefreshListener { _ ->
            onRefresh()
        }
        refreshLayout?.setOnLoadMoreListener { _ ->
            onLoadMore()
        }
    }

    private fun setCollectionsData(
        result: CollectionResponse?,
        adapter: CollectionsAdapter
    ) {
        if (isStartingPage()) {
            if (!result.isNullOrEmpty()) {
                adapter.setList(result)
            } else {
                adapter.setNewInstance(null)
                adapter.setEmptyView(Tools.getEmptyView(requireContext()))
            }
        } else {
            adapter.removeEmptyView()
            if (!result.isNullOrEmpty()) {
                adapter.addData(result)
            } else {
                Logger.d("no more data")
            }
        }
    }

    private fun isStartingPage() = pageNo == UNSPLASH_STARTING_PAGE_INDEX

    private fun onLoadMore() {
        ++pageNo
        obtainCollections()
    }

    private fun onRefresh() {
        pageNo = UNSPLASH_STARTING_PAGE_INDEX
        obtainCollections()
    }

    private fun obtainCollections() = viewModel.obtainCollections(pageNo, perPage)

}