package com.leeeyou.isplash.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeeyou.isplash.data.PhotoDetail
import com.leeeyou.isplash.ext.StatusLiveData
import com.leeeyou.isplash.repository.UnsplashRepository
import com.leeeyou.isplash.state.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PhotoDetailViewModel : ViewModel() {
    val photoDetail: StatusLiveData<PhotoDetail> by lazy { MutableLiveData() }

    fun obtainPhotoDetail(id: String) {
        viewModelScope.launch {
            runCatching {
                photoDetail.value = Status.Loading
                UnsplashRepository.instance.getPhotoDetail(id)
            }.onSuccess {
                photoDetail.value = Status.Success(it)
            }.onFailure {
                photoDetail.value = Status.Error(Exception(it.message))
            }
        }
    }

}