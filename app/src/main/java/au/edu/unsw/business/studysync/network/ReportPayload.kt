package au.edu.unsw.business.studysync.network

data class ReportPayload(val subjectId: String, val reportDate: String, val reports: List<AppReport>)

data class AppReport(val applicationId: String, val usageSeconds: Int)