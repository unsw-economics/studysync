package au.edu.unsw.business.studysync.network

import au.edu.unsw.business.studysync.BuildConfig.SERVER_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(SERVER_URL)
    .build()

interface SyncApiService {
    @FormUrlEncoded
    @POST("api/identify")
    suspend fun identify(@Field("subject_id") subjectId: String): ApiResponse<IdentifyResponse>

    @GET("api/get-group-and-limit")
    suspend fun getGroupAndLimit(@Header("Authorization") authToken: String, @Query("subject_id") subjectId: String): ApiResponse<GetGroupAndLimitResponse>

    @POST("api/submit-report")
    suspend fun submitReport(@Header("Authorization") authToken: String, @Body reportPayload: ReportPayload): BasicApiResponse
}

object SyncApi {
    val service: SyncApiService by lazy {
        retrofit.create(SyncApiService::class.java)
    }
}