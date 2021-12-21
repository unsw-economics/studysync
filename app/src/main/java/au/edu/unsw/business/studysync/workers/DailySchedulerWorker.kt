package au.edu.unsw.business.studysync.workers

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import au.edu.unsw.business.studysync.R
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.constants.Constants.DAILY_SCHEDULER_BOUNCE_WORK
import au.edu.unsw.business.studysync.constants.Constants.DAILY_SCHEDULER_WORK
import au.edu.unsw.business.studysync.constants.Constants.PREFERENCES_NAME
import au.edu.unsw.business.studysync.constants.Constants.RECORD_AND_SUBMIT_WORK
import au.edu.unsw.business.studysync.constants.Constants.STUDY_PHASE_CHANNEL
import au.edu.unsw.business.studysync.constants.Constants.TREATMENT_OVER_NOTIFICATION
import au.edu.unsw.business.studysync.constants.Constants.TREATMENT_START_NOTIFICATION
import au.edu.unsw.business.studysync.constants.Environment.OVER_DATE
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_DATE
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
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

        val firstStep = workManager.beginUniqueWork(RECORD_AND_SUBMIT_WORK, ExistingWorkPolicy.REPLACE, recordRequest)

        val allWork = if (subjectSettings.identified.value!!) {
            val submitRequest = SubmitWorker.createRequest()
            firstStep.then(submitRequest)
        } else {
            firstStep
        }

        allWork.enqueue()

        Log.d("App/DailySchedulerWorker", "work enqueued (record${ if (subjectSettings.identified.value!!) ", submit" else "" })")

        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        if (!tomorrow.isAfter(OVER_DATE)) {
            workManager.enqueueUniqueWork(
                DAILY_SCHEDULER_BOUNCE_WORK,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<DailySchedulerBounceWorker>().build()
            )

            Log.d("App/DailySchedulerWorker", "DailySchedulerBounceWorker enqueued")
        }

        if (subjectSettings.identified.value!! && (today.isEqual(TREATMENT_DATE) || today.isEqual(OVER_DATE))) {
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val (title, text, notificationId) = if (today.isEqual(TREATMENT_DATE)) {
                Triple("New Study Phase", "Please check the app for instructions.", TREATMENT_START_NOTIFICATION)
            } else {
                Triple("Study Completed", "The study is now over. Please check the app for instructions.", TREATMENT_OVER_NOTIFICATION)
            }

            val builder = NotificationCompat.Builder(context, STUDY_PHASE_CHANNEL)
                .setSmallIcon(R.drawable.ic_science)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }

        Log.d("App/DailySchedulerWorker", "success")
        return Result.success()
    }

    companion object {
        fun createRequestForNext0001(): OneTimeWorkRequest {
            val now = ZonedDateTime.now()
            val next0001 = LocalDate.now().plusDays(1).atStartOfDay(ZONE_ID).plusMinutes(1)
            // val next0001 = now.plusSeconds(15)

            return OneTimeWorkRequestBuilder<DailySchedulerWorker>()
                .setInitialDelay(Duration.between(now, next0001))
                .build()
        }
    }
}

class DailySchedulerBounceWorker(private val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(DAILY_SCHEDULER_WORK, ExistingWorkPolicy.REPLACE, DailySchedulerWorker.createRequestForNext0001())
        return Result.success()
    }
}