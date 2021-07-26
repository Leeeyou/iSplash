package com.leeeyou.isplash.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jaeger.library.StatusBarUtil
import com.leeeyou.isplash.R
import com.leeeyou.isplash.data.PhotoDetail
import com.leeeyou.isplash.data.bean.Tag
import com.leeeyou.isplash.ext.observerDefault
import com.leeeyou.isplash.ext.showToast
import com.leeeyou.isplash.util.LoadingUtil
import com.leeeyou.isplash.viewmodel.PhotoDetailViewModel
import kotlinx.android.synthetic.main.fragment_photo_detail.*
import kotlinx.android.synthetic.main.item_loading.*

/**
 * Description: photo detail page
 *
 * Author:      liyou
 * Date:        2021/7/16 5:55 pm
 */
class PhotoDetailFragment : Fragment(R.layout.fragment_photo_detail) {

    private val viewModel: PhotoDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.setTranslucentForImageViewInFragment(activity, null)

        viewModel.photoDetail.observerDefault(this,
            onLoading = {
                LoadingUtil.show(pb_loading)
            },
            onSuccess = {
                renderUi(it)
            },
            onComplete = {
                LoadingUtil.hide(pb_loading)
            }
        )

        val photoId = arguments?.getString("photoId")
        obtainPhotoDetail(photoId)
    }

    private fun renderUi(it: PhotoDetail) {
        context?.let { ctx ->
            Glide.with(ctx).load(it.urls.regular).into(iv_photo as ImageView)

            Glide.with(ctx).load(it.user.profile_image.large)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_avatar as ImageView)

            tv_user_name.text = it.user.name

            val listExif = mutableListOf<Pair<String, String>>()
            listExif.add(
                Pair(
                    "相机",
                    if (TextUtils.isEmpty(it.exif.make) && TextUtils.isEmpty(it.exif.model)) "未知"
                    else if (!TextUtils.isEmpty(it.exif.make)) it.exif.make
                    else if (!TextUtils.isEmpty(it.exif.model)) it.exif.model
                    else it.exif.make + " " + it.exif.model
                )
            )
            listExif.add(
                Pair(
                    "光圈",
                    if (TextUtils.isEmpty(it.exif.aperture)) "未知" else "f/" + it.exif.aperture
                )
            )
            listExif.add(
                Pair(
                    "焦距",
                    if (TextUtils.isEmpty(it.exif.focal_length)) "未知" else it.exif.focal_length + "mm"
                )
            )
            listExif.add(
                Pair(
                    "曝光时间",
                    if (TextUtils.isEmpty(it.exif.exposure_time)) "未知" else it.exif.exposure_time + "s"
                )
            )
            listExif.add(Pair("ISO", if (it.exif.iso == 0) "未知" else it.exif.iso.toString()))
            listExif.add(Pair("尺寸", it.width.toString() + " x " + it.height.toString()))
            rv_exif.adapter =
                object : BaseQuickAdapter<Pair<String, String>, BaseViewHolder>(
                    R.layout.list_item_photo_detail_like,
                    listExif
                ) {
                    override fun convert(
                        holder: BaseViewHolder,
                        item: Pair<String, String>
                    ) {
                        holder.setText(R.id.tv_desc, item.first)
                        holder.setText(R.id.tv_value, item.second)
                    }
                }

            val listLike = mutableListOf<Pair<String, String>>()
            listLike.add(Pair("浏览", it.views.toString()))
            listLike.add(Pair("下载", it.downloads.toString()))
            listLike.add(Pair("喜爱", it.likes.toString()))
            rv_like.adapter =
                object : BaseQuickAdapter<Pair<String, String>, BaseViewHolder>(
                    R.layout.list_item_photo_detail_like,
                    listLike
                ) {
                    override fun convert(
                        holder: BaseViewHolder,
                        item: Pair<String, String>
                    ) {
                        holder.setText(R.id.tv_desc, item.first)
                        holder.setText(R.id.tv_value, item.second)
                    }
                }

            val tagMap = mutableMapOf<String, Tag>()
            it.tags.forEach { tag ->
                tagMap[tag.title] = tag
            }
            tg_category.setTags(it.tags.map { it.title }.toList())
            tg_category.setOnTagClickListener { tag ->
                showToast(tag)
            }

            tv_anchor_1.visibility = View.VISIBLE
            tv_anchor_2.visibility = View.VISIBLE
            tv_anchor_3.visibility = View.VISIBLE
        }
    }

    private fun obtainPhotoDetail(photoId: String?) = run {
        photoId?.let {
            viewModel.obtainPhotoDetail(it)
        }
    }
}