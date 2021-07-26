package com.leeeyou.isplash

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.airbnb.lottie.LottieAnimationView

class Tools {

    companion object {
        fun getEmptyView(context: Context): View {
            val mLayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val emptyView: View =
                mLayoutInflater.inflate(R.layout.common_empty, null, true)
            val lottieLoadingView =
                emptyView.findViewById<View>(R.id.lottie_empty_view) as LottieAnimationView
            lottieLoadingView.setAnimation("halloween_smoothymon.json")
            lottieLoadingView.repeatCount = ValueAnimator.INFINITE
            lottieLoadingView.playAnimation()
            return emptyView
        }
    }

}