package com.edipo2s.gistcomment.di.modules

import android.content.Context
import com.edipo2s.gistcomment.model.remote.IGistRemoteSource
import com.edipo2s.gistcomment.network.CredentialsInterceptor
import com.edipo2s.gistcomment.network.LiveDataCallAdapterFactory
import com.github.simonpercic.oklog3.OkLogInterceptor
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
internal class NetworkModule {

    @Singleton
    @Provides
    fun providesOkHttpClient(context: Context, credentialsInterceptor: CredentialsInterceptor): OkHttpClient {
        val cacheDir = File(context.cacheDir, NetworkModule::class.java.name)
        val cache = Cache(cacheDir, 10 * 1024 * 1024)
        return OkHttpClient.Builder()
                .addNetworkInterceptor(credentialsInterceptor)
                .addNetworkInterceptor(HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(OkLogInterceptor.builder()
                        .withRequestContentType(true)
                        .withRequestHeaders(true)
                        .shortenInfoUrl(true)
                        .build())
                .cache(cache)
                .build()
    }

    @Singleton
    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Singleton
    @Provides
    fun providesMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory = MoshiConverterFactory.create(moshi)
            .withNullSerialization()

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient, moshi: Moshi, moshiConverterFactory: MoshiConverterFactory,
                         liveDataCallAdapterFactory: LiveDataCallAdapterFactory): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl("https://api.github.com")
                .client(okHttpClient)
                .addCallAdapterFactory(liveDataCallAdapterFactory)
                .addConverterFactory(moshiConverterFactory)
                .build()
    }

    @Singleton
    @Provides
    fun providesGistRemoteSource(retrofit: Retrofit): IGistRemoteSource {
        return retrofit.create(IGistRemoteSource::class.java)
    }

}