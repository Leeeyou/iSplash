package com.leeeyou.isplash.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.leeeyou.isplash.R
import com.leeeyou.isplash.data.bean.Collection
import com.leeeyou.isplash.data.bean.Photo

class CollectionsAdapter :
    BaseQuickAdapter<Collection, BaseViewHolder>(R.layout.list_item_collection) {
    override fun convert(holder: BaseViewHolder, item: Collection) {
        holder.setText(R.id.tv_user_name, item.user.name)
        holder.setText(R.id.rv_collection_title, item.title)
        holder.setText(R.id.rv_collection_total, item.total_photos.toString() + " photos")

        Glide.with(context).load(item.preview_photos.getOrNull(0)?.urls?.full)
            .into(holder.getView(R.id.iv_photo) as ImageView)

        Glide.with(context).load(item.user.profile_image.large)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.getView(R.id.iv_avatar) as ImageView)
    }
}