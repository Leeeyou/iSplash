package com.leeeyou.isplash

import android.app.Application
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class ISplashApp : Application() {

    init {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorPrimary, R.color.colorAccent)
            ClassicsHeader(context)
        }
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            val ballPulseFooter = BallPulseFooter(context)
            ballPulseFooter.setBackgroundColor(resources.getColor(R.color.colorBackground))
            ballPulseFooter.setPrimaryColors(resources.getColor(R.color.colorAccent))
            ballPulseFooter
                .setAnimatingColor(context.resources.getColor(R.color.colorSecondary))
                .setNormalColor(context.resources.getColor(R.color.colorSecondary))
        }
    }

}