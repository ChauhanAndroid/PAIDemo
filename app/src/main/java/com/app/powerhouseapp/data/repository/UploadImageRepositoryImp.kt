package com.app.powerhouseapp.data.repository

import com.app.powerhouseapp.data.remote.dto.ImageUploadDto
import com.app.powerhouseapp.data.remote.networkservice.RetrofitService
import com.app.powerhouseapp.domain.repository.UploadImageRepository
import com.app.powerhouseapp.domain.utils.Constants
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadImageRepositoryImp @Inject constructor(
    private val api: RetrofitService
) : UploadImageRepository {
    override suspend fun uploadImage(body: MultipartBody.Part): ImageUploadDto {
        return api.uploadImage(body, Constants.TEST_EMAIL_ID)
    }

    override suspend fun uploadDepthImage(body: MultipartBody.Part): ImageUploadDto {
        return api.uploadDepthImage(body, Constants.TEST_EMAIL_ID)
    }

}