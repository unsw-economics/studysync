package au.edu.unsw.business.studysync.usage

import android.content.Context
import android.util.Log
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.database.AppDatabase
import au.edu.unsw.business.studysync.database.DbAppReport
import au.edu.unsw.business.studysync.database.DbReport
import au.edu.unsw.business.studysync.network.ReportPayload
import au.edu.unsw.business.studysync.network.ServerAppReport
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.support.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap

class UsageDriver(private val context: Context, private val settings: SubjectSettings) {
    private val database = AppDatabase.getDatabase(context)
    private val reportDao = database.reportDao()

    suspend fun submitUnsyncedReports() {
        val payloadMap = getUnsyncedReportPayloads()
        val authToken = settings.authToken.value!!

        withContext(Dispatchers.IO) {
            for ((_, payload) in payloadMap) {
                Log.d("MainActivity", "Submitting $payload")
                SyncApi.service.submitReport(authToken, payload)

                Log.d("MainActivity", "Submitted")
                reportDao.markReportSynced(payload.period, payload.day)
            }
        }
    }

    private suspend fun getUnsyncedReportPayloads(): Map<Pair<String, Int>, ReportPayload> {
        val unsyncedAppReports = withContext(Dispatchers.IO) {
            reportDao.getUnsyncedAppReports()
        }

        val subjectId = settings.subjectId.value!!
        val payloadMap: MutableMap<Pair<String, Int>, ReportPayload> = HashMap()

        for (appReport in unsyncedAppReports) {
            val period = appReport.period
            val day = appReport.day

            val periodDay = Pair(appReport.period, appReport.day)

            if (!payloadMap.contains(periodDay)) {
                payloadMap[periodDay] = ReportPayload(subjectId, period, day, LinkedList())
            }

            val serverAppReport =
                ServerAppReport(appReport.applicationName, appReport.usageSeconds)
            (payloadMap[periodDay]!!.reports as MutableList).add(serverAppReport)
        }

        return payloadMap
    }

    suspend fun recordNewUsages() {
        var date = settings.lastRecorded.value!!
        val today = LocalDate.now()

        val reports: MutableList<DbReport> = LinkedList()
        val appReports: MutableList<DbAppReport> = LinkedList()

        while (date.isBefore(today)) {
            val nextDate = date.plusDays(1)
            val usage = UsageStatsAnalyzer.computeUsage(
                context,
                TimeUtils.toMilliseconds(date),
                TimeUtils.toMilliseconds(nextDate)
            )
            val (period, day) = TimeUtils.getStudyPeriodAndDay(date)

            reports.add(
                DbReport(
                    period,
                    day
                )
            )

            for ((appName, usage_ms) in usage) {
                appReports.add(DbAppReport(period, day, appName, usage_ms / 1000))
            }

            date = nextDate
        }

        withContext(Dispatchers.IO) {
            reportDao.insertMultipleDayReports(reports, appReports)
        }

        settings.setLastRecorded(today)
    }

    fun computeTodayUsage(): Duration {
        val usageMap = UsageStatsAnalyzer.computeUsage(context, TimeUtils.midnight(), TimeUtils.now())
        return Duration.ofMillis(usageMap.map { it.value }.sum())
    }

    suspend fun computeTotalEarned(): Double {
        val appReports = withContext(Dispatchers.IO) {
            reportDao.getExperimentAppReports()
        }

        val dailyUsageMap: MutableMap<Int, Long> = HashMap()

        for (appReport in appReports) {
            dailyUsageMap[appReport.day] = dailyUsageMap.getOrDefault(appReport.day, 0) + appReport.usageSeconds
        }

        val limit = settings.treatmentLimit.value!!.seconds

        return dailyUsageMap
            .filter { it.value < limit }
            .map { it.value }
            .count() * settings.testGroup.value!! * 0.5
    }
}