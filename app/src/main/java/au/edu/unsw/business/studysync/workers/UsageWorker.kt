package au.edu.unsw.business.studysync.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.constants.Constants
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.network.UsagePayload
import au.edu.unsw.business.studysync.support.TimeUtils
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.acra.ACRA
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class UsageWorker(private val context: Context, private val params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("App/UsageWorker", "begin")

        if (params.runAttemptCount > 20) {
            Log.d("App/UsageWorker", "failing after 20 attempts")
            return Result.failure()
        }

        return try {
            //val lastRecorded = withContext(Dispatchers.Main) {
            //    SubjectSettings(context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)).lastRecorded.value
            //}

            // Skip if we've already recorded usage for today
            //if (lastRecorded == TimeUtils.nowLD()) {
            //    Log.d("App/UsageWorker", "skipping, already recorded today")
            //    return Result.success()
            //}

            // Calculate the first date which should be either 28 days before today or the baseline start date.
            var date = if (TimeUtils.studyDates.baselineDate.isBefore(TimeUtils.nowLD().minusDays(28)))
                 TimeUtils.nowLD().minusDays(28) else TimeUtils.studyDates.baselineDate
            Log.d("App/UsageWorker", "date: $date")
            val today = TimeUtils.nowLD()

            // Create a hashmap with the date as the key and the number of seconds of usage as the value.
            val usageMap = HashMap<String, Long>()

            while (date.isBefore(today)) {
                val nextDate = date.plusDays(1)
                val usage = UsageStatsAnalyzer.computeUsage(
                    context,
                    TimeUtils.toMilliseconds(date),
                    TimeUtils.toMilliseconds(nextDate)
                )

                // Initialise the usageMap with the date.
                if (!usageMap.containsKey(date.toString())) {
                    usageMap[date.toString()] = 0L
                }

                // Add the usage to the usageMap.
                for ((_, usageMilliseconds) in usage) {
                    usageMap[date.toString()] = usageMap[date.toString()]!! + usageMilliseconds / 1000
                }

                date = nextDate
            }

            // Submit the usageMap to the api.
            withContext(Dispatchers.Main) {
                val subjectSettings = SubjectSettings(context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE))
                SyncApi.service.submitUsage(subjectSettings.authToken.value!!, UsagePayload(subjectSettings.subjectId.value!!, usageMap))
            }

            Log.d("App/UsageWorker", "success")
            Result.success()
        } catch (ex: Exception) {
            ACRA.errorReporter.handleSilentException(ex)
            Log.d("App/UsageWorker", "retry")
            Result.retry()
        }
    }

    companion object {
        private val NETWORK_CONSTRAINT = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun createRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<UsageWorker>(12, TimeUnit.HOURS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(1))
                .setConstraints(NETWORK_CONSTRAINT)
                .build()
        }
    }
}