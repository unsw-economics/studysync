package au.edu.unsw.business.studysync.network

import com.squareup.moshi.Json
import retrofit2.http.Field

data class ReportPayload(
    @Json(name = "subject_id") val subjectId: String,
    val period: String,
    val day: Int,
    val reports: List<AppReport>
)

data class AppReport(
    @Json(name = "application_name") val applicationName: String,
    @Json(name = "usage_seconds") val usageSeconds: Long
)