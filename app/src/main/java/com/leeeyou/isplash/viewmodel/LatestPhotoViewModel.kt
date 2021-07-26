package com.leeeyou.isplash.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeeyou.isplash.data.PhotoResponse
import com.leeeyou.isplash.ext.StatusLiveData
import com.leeeyou.isplash.repository.UnsplashRepository
import com.leeeyou.isplash.state.Status
import kotlinx.coroutines.launch

class LatestPhotoViewModel : ViewModel() {
    val photoList: StatusLiveData<PhotoResponse> by lazy { MutableLiveData() }

    fun obtainLatestPhotoList(page: Int, perPage: Int = 10) {
        viewModelScope.launch {
            runCatching {
                photoList.value = Status.Loading
                UnsplashRepository.instance.getLatestPhoto(page, perPage)
            }.onSuccess {
                photoList.value = Status.Success(it)
            }.onFailure {
                photoList.value = Status.Error(Exception(it.message))
            }
        }
    }
}