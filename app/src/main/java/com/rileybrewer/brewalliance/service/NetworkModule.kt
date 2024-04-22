package com.rileybrewer.brewalliance.service

import com.rileybrewer.brewalliance.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providesBlueAllianceService(): BlueAllianceService {
        // Apply authentication header
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .addHeader(AUTH_HEADER, BuildConfig.TBA_KEY)
                        .build()
                )
            }
            .build()

        // Use Moshi converter to get objects from JSON
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(BlueAllianceService::class.java)

    }

    companion object {
        private const val BASE_URL = "https://www.thebluealliance.com/api/v3/"
        private const val AUTH_HEADER = "X-TBA-Auth-Key"
    }
}