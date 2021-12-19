package com.app.powerhouseapp.domain.use_case

import android.util.Log
import com.app.powerhouseapp.common.NetworkResult
import com.app.powerhouseapp.data.remote.dto.toConfig
import com.app.powerhouseapp.data.remote.dto.toImageUpload
import com.app.powerhouseapp.domain.model.Config
import com.app.powerhouseapp.domain.model.ImageUpload
import com.app.powerhouseapp.domain.repository.ConfigRepository
import com.app.powerhouseapp.domain.repository.UploadImageRepository
import com.app.powerhouseapp.domain.utils.ErrorMessage.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.app.powerhouseapp.domain.utils.ErrorMessage.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class GetImageUpload @Inject constructor(
    private val repository: UploadImageRepository
) {

    /**
     * Operator function to invoke uploadImage
     */
    operator fun invoke(body: MultipartBody.Part): Flow<NetworkResult<ImageUpload>> = flow {
        try {
            emit(NetworkResult.Loading())
            val result = repository.uploadImage(body).toImageUpload()
            Log.e("resultImageUpload","${result.message}")
            emit(NetworkResult.Success(result))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: UNKNOWN_ERROR))
        } catch (e: IOException) {
            emit(NetworkResult.Error(ERROR_CHECK_NETWORK_CONNECTION))
        }
    }
}