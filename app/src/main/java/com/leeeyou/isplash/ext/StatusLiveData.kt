package com.leeeyou.isplash.ext

import androidx.lifecycle.MutableLiveData
import com.leeeyou.isplash.state.Status

typealias StatusLiveData<T> = MutableLiveData<Status<T>>