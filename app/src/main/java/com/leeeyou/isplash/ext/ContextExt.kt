package com.leeeyou.isplash.ext

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils

/**
 * 设置onclick事件
 */
fun setViewsOnClickListener(listener: View.OnClickListener, vararg views: View) {
    views.forEach { it.setOnClickListener(listener) }
}


/**
 * context显示toast
 */
fun Context.showToast(tips: String) {
    ToastUtils.showShort(tips)
}

/**
 * Fragment显示toast
 */
fun Fragment.showToast(tips: String) {
    ToastUtils.showShort(tips)
}

/**
 * 隐藏键盘
 */
fun Context.hideSoftInput() {
    val imm: InputMethodManager =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm?.hideSoftInputFromWindow((this as Activity).window.decorView.windowToken, 0)
}