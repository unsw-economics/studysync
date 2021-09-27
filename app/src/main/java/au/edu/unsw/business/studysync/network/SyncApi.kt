package au.edu.unsw.business.studysync.network

import au.edu.unsw.business.studysync.constants.Constants
import au.edu.unsw.business.studysync.constants.Environment
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Environment.SERVER_URL)
    .build()

interface SyncApiService {
    @FormUrlEncoded
    @POST("api/identify")
    suspend fun identify(@Field("subject_id") subjectId: String): ApiResponse<IdentifyResponse>

    @GET("api/get-test-group")
    suspend fun getTestGroup(@Header("Authorization") authToken: String, @Query("subject_id") subjectId: String): ApiResponse<GetTestGroupResponse>

    @POST("api/submit-report")
    suspend fun submitReport(@Header("Authorization") authToken: String, @Body reportPayload: ReportPayload): BasicApiResponse
}

object SyncApi {
    val service: SyncApiService by lazy {
        retrofit.create(SyncApiService::class.java)
    }
}