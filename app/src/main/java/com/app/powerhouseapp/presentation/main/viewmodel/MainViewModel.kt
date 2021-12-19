package com.app.powerhouseapp.presentation.main.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.powerhouseapp.common.NetworkResult
import com.app.powerhouseapp.domain.use_case.GetConfig
import com.app.powerhouseapp.domain.use_case.GetImageUpload
import com.app.powerhouseapp.domain.utils.Constants
import com.app.powerhouseapp.domain.utils.ErrorMessage
import com.app.powerhouseapp.presentation.main.viewmodel.state.ConfigState
import com.app.powerhouseapp.presentation.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import timber.log.Timber
import javax.inject.Inject
import okhttp3.MultipartBody

import okhttp3.RequestBody
import java.io.File


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getConfig: GetConfig, private val getImageUpload: GetImageUpload
) : ViewModel() {

    //observable for config API results
    private val _responseConfig: MutableLiveData<ConfigState> = MutableLiveData()

    val responseConfig: LiveData<ConfigState> = _responseConfig


    /**
     * Function to configure
     */

    fun getConfig() {
        getConfig.invoke(inputBodyConstruct()).onEach { result ->
            when (result) {
                is NetworkResult.Success -> {
                    Log.e("Success", "$result")
                    _responseConfig.value =  ConfigState(success = "Success")
                }
                is NetworkResult.Error -> {
                    _responseConfig.value = ConfigState(
                        error = result.message ?: ErrorMessage.UNKNOWN_ERROR
                    )
                }
                is NetworkResult.Loading -> {
                    _responseConfig.value = ConfigState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Function to upload image
     */
    fun uploadImage(file:File?) {
        getImageUpload.invoke(inputBodyUploadImage(file)).onEach { result ->
            when (result) {
                is NetworkResult.Success -> {
                    Log.e("Success", "${result.data?.message}")
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        }.launchIn(viewModelScope)
    }


    /**
     * Create inputBodyConstruct
     */
    private fun inputBodyConstruct(): HashMap<String, String> {
        var bodyMap: HashMap<String, String> = HashMap()
        bodyMap["email_id"] = Constants.TEST_EMAIL_ID
        Log.e("params==>","$bodyMap")
        return bodyMap
    }


    /**
     * Create uploadBody
     */
    private fun inputBodyUploadImage(file: File?): MultipartBody.Part {
        val type = FileUtils.getMimeType(file.toString())
        var requestFile: RequestBody = RequestBody.create(
            MediaType.parse(type),
            file
        )
        // MultipartBody.Part is used to send also the actual file name
        var body = MultipartBody.Part.createFormData("file", file?.name, requestFile)
        Log.e("params==>","$body")
        return body
    }

}