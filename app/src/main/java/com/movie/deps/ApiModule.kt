package com.movie.deps

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.movie.BuildConfig
import com.movie.api.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Modifier
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    companion object {
        private const val BEARER: String = "Bearer"
        private const val ACCEPT: String = "Accept"
        private const val APP_JSON: String = "application/json"
        private const val NO_AUTH: String = "No-Authentication: true"
        private const val CONTENT_TYPE: String = "Content-Type: application/json"
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .readTimeout(BuildConfig.TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(BuildConfig.TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                    .header(ACCEPT, APP_JSON)
                    .header(ACCEPT, CONTENT_TYPE)
                return@addInterceptor chain.proceed(requestBuilder.build())
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}