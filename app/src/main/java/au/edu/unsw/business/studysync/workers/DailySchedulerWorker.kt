package au.edu.unsw.business.studysync.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import au.edu.unsw.business.studysync.constants.Constants.DAILY_SCHEDULER_WORK
import au.edu.unsw.business.studysync.constants.Constants.RECORD_AND_SUBMIT_WORK
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
import au.edu.unsw.business.studysync.support.TimeUtils
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime

class DailySchedulerWorker(private val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val recordRequest = RecordWorker.createRequest()
        val submitRequest = SubmitWorker.createRequest()

        val wm = WorkManager.getInstance(context)

        wm.beginUniqueWork(RECORD_AND_SUBMIT_WORK, ExistingWorkPolicy.KEEP, recordRequest)
            .then(submitRequest)
            .enqueue()

        Log.d("MainWork", "record and submit enqueued")

        val now = ZonedDateTime.now()
        val tomorrow = LocalDate.now().plusDays(1)
        val delay = Duration.between(now, tomorrow.atStartOfDay(ZONE_ID))

        Log.d("MainWork", "enqueueing daily scheduler with time delay ${TimeUtils.digitalTimeHm(delay)}")

        val nextRequest = createRequest(delay)

        wm.enqueueUniqueWork(DAILY_SCHEDULER_WORK, ExistingWorkPolicy.APPEND_OR_REPLACE, nextRequest)

        Log.d("MainWork", "daily scheduler enqueued")

        return Result.success()
    }

    companion object {
        fun createRequest(delay: Duration): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DailySchedulerWorker>()
                .setInitialDelay(delay)
                .build()
        }

        fun createRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DailySchedulerWorker>()
                .build()
        }
    }
}