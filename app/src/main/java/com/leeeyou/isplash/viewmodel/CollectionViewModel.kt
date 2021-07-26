package com.leeeyou.isplash.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeeyou.isplash.data.CollectionResponse
import com.leeeyou.isplash.ext.StatusLiveData
import com.leeeyou.isplash.repository.UnsplashRepository
import com.leeeyou.isplash.state.Status
import kotlinx.coroutines.launch

class CollectionViewModel : ViewModel() {
    val collections: StatusLiveData<CollectionResponse> by lazy { MutableLiveData() }

    fun obtainCollections(page: Int, perPage: Int = 10) {
        viewModelScope.launch {
            runCatching {
                collections.value = Status.Loading
                UnsplashRepository.instance.getCollections(page, perPage)
            }.onSuccess {
                collections.value = Status.Success(it)
            }.onFailure {
                collections.value = Status.Error(Exception(it.message))
            }
        }
    }
}