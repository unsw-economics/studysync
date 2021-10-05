package au.edu.unsw.business.studysync.network

import com.squareup.moshi.Json

data class ApiResponse<T>(
    val message: String?,
    val data: T?
)

data class BasicApiResponse(
    val message: String?
)

data class IdentifyResponse(
    @Json(name = "auth_token")
    val authToken: String
)

data class GetGroupAndLimitResponse(
    @Json(name = "test_group")
    val testGroup: Int?,
    @Json(name = "treatment_limit")
    val treatmentLimit: Int?
)
