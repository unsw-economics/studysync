package au.edu.unsw.business.studysync.network

import com.squareup.moshi.Json
import retrofit2.http.Field

data class ReportPayload(
    @Json(name = "subject_id") val subjectId: String,
    @Json(name = "report_period") val reportPeriod: String,
    @Json(name = "report_day") val reportDay: Int,
    val reports: List<AppReport>
)

data class AppReport(
    @Json(name = "application_id") val applicationId: String,
    @Json(name = "usage_seconds") val usageSeconds: Long
)