package au.edu.unsw.business.studysync

import android.content.Context
import androidx.work.*
import au.edu.unsw.business.studysync.constants.Environment.DAILY_REPORT_WORKER_TAG
import au.edu.unsw.business.studysync.constants.Environment.NETWORK_CONSTRAINT
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE_MIDNIGHT
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class ReportDailyWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    private fun scheduleRequest() {

    }

    override fun doWork(): Result {
        // Set execution around 12AM
        // Calculate difference between now and treatment start midnight
        val timeDiff = ChronoUnit.MILLIS.between(ZonedDateTime.now(),
            TREATMENT_START_DATE_MIDNIGHT.minusHours(23).minusMinutes(30))

        val dailyWorkRequest = PeriodicWorkRequestBuilder<ReportDailyWorker>(
            24, TimeUnit.HOURS,
            15, TimeUnit.MINUTES
        )
            .setConstraints(NETWORK_CONSTRAINT)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(DAILY_REPORT_WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest)

        return Result.success()
    }


}