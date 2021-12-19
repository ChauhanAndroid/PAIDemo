package com.app.powerhouseapp.data.remote.networkservice

import com.app.powerhouseapp.data.remote.dto.ConfigDto
import com.app.powerhouseapp.data.remote.dto.ImageUploadDto
import okhttp3.MultipartBody
import retrofit2.http.*

interface RetrofitService {

    @POST("config")
    suspend fun configureEmail(@Body map:HashMap<String,String>
    ): ConfigDto

    @Multipart
    @POST("upload_image")
    suspend fun uploadImage(@Part image: MultipartBody.Part, @Query("email_id") email_id: String
    ): ImageUploadDto

    @Multipart
    @POST("upload_depth")
    suspend fun uploadDepthImage(@Part image: MultipartBody.Part, @Query("email_id") email_id: String
    ): ImageUploadDto

}
