package au.edu.unsw.business.studysync.network

import com.squareup.moshi.Json

data class UsagePayload(
    @Json(name = "subject_id") val subjectId: String,
    val usage: Map<String, Long>
)