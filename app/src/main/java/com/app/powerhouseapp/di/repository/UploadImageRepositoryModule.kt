package com.app.powerhouseapp.di.repository

import com.app.powerhouseapp.data.remote.networkservice.RetrofitService
import com.app.powerhouseapp.data.repository.ConfigRepositoryImp
import com.app.powerhouseapp.data.repository.UploadImageRepositoryImp
import com.app.powerhouseapp.domain.repository.ConfigRepository
import com.app.powerhouseapp.domain.repository.UploadImageRepository
import com.app.powerhouseapp.domain.use_case.GetConfig
import com.app.powerhouseapp.domain.use_case.GetImageUpload
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UploadImageRepositoryModule{

    @Singleton
    @Provides
    fun provideUploadImageRepository( api: RetrofitService
    ): UploadImageRepository {
        return UploadImageRepositoryImp(api)
    }

    @Singleton
    @Provides
    fun provideGetImageUpload( uploadImageRepository: UploadImageRepository
    ): GetImageUpload {
        return GetImageUpload(uploadImageRepository)
    }

}