package com.leeeyou.isplash.util

import android.view.View
import android.widget.ProgressBar
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.leeeyou.isplash.R

object LoadingUtil {
    @JvmStatic
    fun show(progressBar: ProgressBar) {
        val foldingCube = DoubleBounce()
        foldingCube.color = progressBar.context.resources.getColor(R.color.colorPrimary)
        progressBar.indeterminateDrawable = foldingCube
        progressBar.visibility = View.VISIBLE
    }

    @JvmStatic
    fun hide(progressBar: ProgressBar) {
        progressBar.visibility = View.INVISIBLE
    }
}