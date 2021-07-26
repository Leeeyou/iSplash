package com.leeeyou.isplash.ext

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.leeeyou.isplash.state.Status
import com.orhanobut.logger.Logger.d

@MainThread
inline fun <T> StatusLiveData<T>.observerDefault(
    fragment: Fragment,
    tips: Boolean? = true,
    crossinline onLoading: (() -> Unit) = {},
    crossinline onSuccess: ((T) -> Unit),
    crossinline onComplete: (() -> Unit) = {}
) {
    observe(fragment) {
        when (it) {
            is Status.Loading -> {
                onLoading()
            }
            is Status.Success -> {
                onSuccess(it.data)
                onComplete()
            }
            is Status.Error -> {
                d(it.error)
                if (tips!!) it.error.message?.also { msg -> fragment.context?.showToast(msg) }
                onComplete()
            }
        }
    }
}