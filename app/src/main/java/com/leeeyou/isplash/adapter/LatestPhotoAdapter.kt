package com.leeeyou.isplash.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.leeeyou.isplash.R
import com.leeeyou.isplash.data.bean.Photo

class LatestPhotoAdapter :
    BaseQuickAdapter<Photo, BaseViewHolder>(R.layout.list_item_latest_photo) {
    override fun convert(holder: BaseViewHolder, item: Photo) {
        holder.setText(R.id.tv_user_name, item.user.name)
        holder.setGone(R.id.tv_sponsor, item.sponsorship == null)
        Glide.with(context).load(item.urls.regular).into(holder.getView(R.id.iv_photo) as ImageView)
        Glide.with(context).load(item.user.profile_image.large)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.getView(R.id.iv_avatar) as ImageView)
    }
}
