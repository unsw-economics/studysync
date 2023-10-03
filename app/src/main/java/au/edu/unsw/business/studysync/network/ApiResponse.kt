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
    val authToken: String,
    @Json(name = "subject_id")
    val subjectId: String
)

data class GetTestParamsResponse(
    @Json(name = "test_group")
    val testGroup: Int?,
    @Json(name = "treatment_intensity")
    val treatmentIntensity: Int?,
    @Json(name = "treatment_limit")
    val treatmentLimit: Int?
)

data class GetDatesResponse(
    @Json(name = "baseline_date")
    val baselineDate: String?,
    @Json(name = "treatment_date")
    val treatmentDate: String?,
    @Json(name = "endline_date")
    val endlineDate: String?,
    @Json(name = "over_date")
    val overDate: String?
)