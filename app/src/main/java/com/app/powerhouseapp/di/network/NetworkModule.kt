package com.app.powerhouseapp.di.network

import com.app.powerhouseapp.data.remote.interceptors.HeaderInterceptor
import com.app.powerhouseapp.data.remote.networkservice.RetrofitService
import com.app.powerhouseapp.domain.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesHeaderInterceptor() = HeaderInterceptor()

    @Singleton
    @Provides
    fun provideHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder().apply {
                //add headers at one place
                addInterceptor(
                    headerInterceptor
                )

                //debug logging interceptor
//                addInterceptor(
//                    httpLoggingInterceptor
//                )
            }
            .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideConverterFactoryScalar(): ScalarsConverterFactory =
        ScalarsConverterFactory.create()

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        scalarsConverterFactory: ScalarsConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.DOMAIN_URL)
            .client(okHttpClient).addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideNetworkService(retrofit: Retrofit): RetrofitService =
        retrofit.create(RetrofitService::class.java)

}