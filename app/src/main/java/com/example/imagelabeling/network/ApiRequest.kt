package com.example.imagelabeling.network
import com.example.imagelabeling.data.Image
import com.example.myapplication.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


private const val BASE_URL = "https://api.unsplash.com/"
const val CLIENT_ID = BuildConfig.UNSPLASH_ACCESS_KEY

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val networkLoggingInterceptor =
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
val retrofit = Retrofit.Builder()
    .client(OkHttpClient.Builder().addInterceptor(networkLoggingInterceptor).build())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()
interface ApiRequest {
    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("photos/random")
    suspend fun getRandomImage(
        @Query("client_id")
        accessKey: String,
        @Query("page")
        page: Int,
        @Query("per_page")
        perPage: Int
    ): Image

}

object PhotoApi {
    val retrofitService: ApiRequest by lazy { retrofit.create(ApiRequest::class.java) }
}