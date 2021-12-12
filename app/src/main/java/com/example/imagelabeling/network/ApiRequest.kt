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

    /*@GET("photos/random")
    fun getRandomPhotos(@Query("category") categoryId: Int?,
                        @Query("featured") featured: Boolean?,
                        @Query("username") username: String,
                        @Query("query") query: String,
                        @Query("orientation") orientation: String,
                        @Query("count") count: Int): Call<List<Photos>>*/

  /*@GET("/photos/random?")
    fun getRandomImages(
        @Query("count") count: String = "30",
        @Query("client_id") client_id: String = API_KEY
    ): Call<List<Image>>*/

    /*@Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query")
        query: String,
        @Query("page")
        page: Int,
        @Query("per_page")
        perPage: Int
    ): UnsplashResponse*/


}

object PhotoApi {
    val retrofitService: ApiRequest by lazy { retrofit.create(ApiRequest::class.java) }
}