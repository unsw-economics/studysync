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
import java.util.*

class UsageDriver(private val context: Context, private val settings: SubjectSettings) {
    private val database = AppDatabase.getDatabase(context)
    private val reportDao = database.reportDao()

    suspend fun submitUnsyncedReports() {
        val payloadMap = getUnsyncedReportPayloads()
        val authToken = settings.authToken.value!!

        withContext(Dispatchers.IO) {
            for ((_, payload) in payloadMap) {
                Log.d("App/UsageDriver", "submitting $payload")
                SyncApi.service.submitReport(authToken, payload)

                Log.d("App/UsageDriver", "submitted")
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
        val today = TimeUtils.nowLD()

        val reports: MutableList<DbReport> = LinkedList()
        val appReports: MutableList<DbAppReport> = LinkedList()

        Log.d("App/UsageDriver", "$date")
        Log.d("App/UsageDriver", "$today")

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

            for ((appName, usageMilliseconds) in usage) {
                appReports.add(DbAppReport(period, day, appName, usageMilliseconds / 1000))
            }

            date = nextDate
        }

        withContext(Dispatchers.IO) {
            reportDao.insertMultipleDayReports(reports, appReports)
        }

        withContext(Dispatchers.Main) {
            settings.setLastRecorded(today)
        }
    }

    fun computeTodayUsage(): Duration {
        val usage = UsageStatsAnalyzer.computeUsage(context, TimeUtils.midnight(), TimeUtils.now())
        return Duration.ofMillis(usage.map { it.value }.sum())
    }

    suspend fun countSuccesses(): Int {
        val appReports = withContext(Dispatchers.IO) {
            reportDao.getExperimentAppReports()
        }

        val dailyUsageMap: MutableMap<Int, Long> = HashMap()

        for (appReport in appReports) {
            dailyUsageMap[appReport.day] = dailyUsageMap.getOrDefault(appReport.day, 0) + appReport.usageSeconds
        }

        val limit = settings.treatmentLimit.value!!.seconds

        return dailyUsageMap
            .filter {
                Log.d("App/UsageDriver", "Day ${it.key} usage: ${TimeUtils.digitalTimeHm(Duration.ofSeconds(it.value))}")
                it.value < limit }
            .map { it.value }
            .count()
    }
}