package au.edu.unsw.business.studysync.network

import com.squareup.moshi.Json

data class ReportPayload(
    @Json(name = "subject_id") val subjectId: String,
    val period: String,
    val day: Int,
    val reports: List<ServerAppReport>
)

data class ServerAppReport(
    @Json(name = "application_name") val applicationName: String,
    @Json(name = "usage") val usageSeconds: Long
)