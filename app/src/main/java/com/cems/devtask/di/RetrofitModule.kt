package com.cems.devtask.di

import android.content.Context
import com.cems.devtask.BuildConfig
import com.cems.devtask.helper.extensions.isNetworkConnected
import com.cems.devtask.repository.network.api.ApiService
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

private const val CACHE_FILE_SIZE: Long = 5 * 1024 * 1024

val retrofitModule = module {

    single<Call.Factory> {
        val cacheFile = cacheFile(androidContext())
        val cache = cache(cacheFile)
        okHttp(cache, androidContext())
    }

    single {
        retrofit(get())
    }

    single {
        get<Retrofit>().create(ApiService::class.java)
    }

}

private fun getLoggingInterceptor(): Interceptor =
    HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
    }

private fun getResponseInterceptor(): Interceptor = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val cacheControl = originalResponse.header("Cache-Control")
        return if (cacheControl == null || cacheControl.contains("no-store") ||
            cacheControl.contains("no-cache") || cacheControl.contains("must-revalidate")
            || cacheControl.contains("max-age=0")
        ) originalResponse.newBuilder()
            .removeHeader("Pragma")
            .header("Cache-Control", "public, max-age=" + 5)
            .build()
        else originalResponse
    }
}

private fun getOfflineResponseInterceptor(context: Context): Interceptor = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!context.isNetworkConnected()) {
            request = request.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                .build()
        }
        return chain.proceed(request)
    }
}

private fun cacheFile(context: Context) = File(context.cacheDir, "app").apply {
    if (!this.exists())
        mkdirs()
}

private fun cache(cacheFile: File) = Cache(cacheFile, CACHE_FILE_SIZE)

private fun retrofit(callFactory: Call.Factory) = Retrofit.Builder()
    .callFactory(callFactory)
    .baseUrl(BuildConfig.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

private fun okHttp(cache: Cache, context: Context) = OkHttpClient.Builder()
    .cache(cache)
    .addNetworkInterceptor(getResponseInterceptor())
//    .addInterceptor(getOfflineResponseInterceptor(context))
    .addInterceptor(getLoggingInterceptor())
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()
