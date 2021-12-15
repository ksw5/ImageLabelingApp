package com.example.imagelabeling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagelabeling.data.Image
import com.example.imagelabeling.network.ApiResponse
import com.example.imagelabeling.network.PhotoApi
import com.example.myapplication.BuildConfig
import kotlinx.coroutines.launch

const val CLIENT_ID = BuildConfig.UNSPLASH_ACCESS_KEY

class ImageViewModel: ViewModel() {

    private val _apiResponse = MutableLiveData<Image>()
    val apiResponse: LiveData<Image> = _apiResponse

    fun getRandomPhoto() {
        viewModelScope.launch {
            _apiResponse.value = PhotoApi.retrofitService.getRandomImage(CLIENT_ID, 1, 1)
        }
    }
}