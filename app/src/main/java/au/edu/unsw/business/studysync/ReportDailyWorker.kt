package au.edu.unsw.business.studysync

import android.content.Context
import androidx.work.*
import au.edu.unsw.business.studysync.constants.Environment.DAILY_REPORT_WORKER_TAG
import au.edu.unsw.business.studysync.constants.Environment.NETWORK_CONSTRAINT
import java.util.concurrent.TimeUnit

class ReportDailyWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    private fun scheduleRequest() {

    }

    override fun doWork(): Result {
        // Set execution around 12AM
        // TODO Calculate difference between now and treatment start midnight
        val timeDiff = 0L
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