package com.app.powerhouseapp.di.repository

import com.app.powerhouseapp.data.remote.networkservice.RetrofitService
import com.app.powerhouseapp.data.repository.ConfigRepositoryImp
import com.app.powerhouseapp.domain.repository.ConfigRepository
import com.app.powerhouseapp.domain.use_case.GetConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigRepositoryModule{

    @Singleton
    @Provides
    fun provideConfigRepository( api: RetrofitService
    ): ConfigRepository {
        return ConfigRepositoryImp(api)
    }

    @Singleton
    @Provides
    fun provideGetConfig( configRepository: ConfigRepository
    ): GetConfig {
        return GetConfig(configRepository)
    }

}