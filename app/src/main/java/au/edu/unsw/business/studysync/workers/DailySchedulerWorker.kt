package au.edu.unsw.business.studysync.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.constants.Constants.DAILY_SCHEDULER_WORK
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_OVER
import au.edu.unsw.business.studysync.constants.Constants.PREFERENCES_NAME
import au.edu.unsw.business.studysync.constants.Constants.RECORD_AND_SUBMIT_WORK
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
import au.edu.unsw.business.studysync.support.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime

class DailySchedulerWorker(private val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("App/DailySchedulerWorker", "begin")

        val recordRequest = RecordWorker.createRequest()

        val subjectSettings = withContext(Dispatchers.Main) {
            SubjectSettings(context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE))
        }
        val workManager = WorkManager.getInstance(context)

        val firstStep = workManager.beginUniqueWork(RECORD_AND_SUBMIT_WORK, ExistingWorkPolicy.KEEP, recordRequest)

        val allWork = if (subjectSettings.identified.value!!) {
            val submitRequest = SubmitWorker.createRequest()
            firstStep.then(submitRequest)
        } else {
            firstStep
        }

        allWork.enqueue()

        Log.d("App/DailySchedulerWorker", "work enqueued (record${ if (subjectSettings.identified.value!!) ", submit" else "" }) for TODO")

        val tomorrow = LocalDate.now().plusDays(1)

        if (TimeUtils.getPeriod(tomorrow) != PERIOD_OVER) {

            val now = ZonedDateTime.now()
            val delay = Duration.between(now, tomorrow.atStartOfDay(ZONE_ID))

            val nextRequest = createRequest(delay)

            workManager.enqueueUniqueWork(
                DAILY_SCHEDULER_WORK,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                nextRequest
            )

            Log.d("App/DailySchedulerWorker", "DailySchedulerWorker enqueued")
        }

        Log.d("App/DailySchedulerWorker", "success")
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