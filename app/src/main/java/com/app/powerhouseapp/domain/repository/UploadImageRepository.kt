package com.app.powerhouseapp.domain.repository

import com.app.powerhouseapp.data.remote.dto.ImageUploadDto
import okhttp3.MultipartBody

interface UploadImageRepository {

    suspend fun uploadImage(body: MultipartBody.Part): ImageUploadDto

    suspend fun uploadDepthImage(body: MultipartBody.Part): ImageUploadDto

}