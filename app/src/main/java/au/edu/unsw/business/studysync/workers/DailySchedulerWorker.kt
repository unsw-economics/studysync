package au.edu.unsw.business.studysync.workers

import android.Manifest
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import au.edu.unsw.business.studysync.R
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.constants.Constants.DAILY_CHECK_STUDY_DATES
import au.edu.unsw.business.studysync.constants.Constants.DAILY_SCHEDULER_BOUNCE_WORK
import au.edu.unsw.business.studysync.constants.Constants.DAILY_SCHEDULER_WORK
import au.edu.unsw.business.studysync.constants.Constants.PREFERENCES_NAME
import au.edu.unsw.business.studysync.constants.Constants.RECORD_AND_SUBMIT_WORK
import au.edu.unsw.business.studysync.constants.Constants.STUDY_PHASE_CHANNEL
import au.edu.unsw.business.studysync.constants.Constants.TREATMENT_OVER_NOTIFICATION
import au.edu.unsw.business.studysync.constants.Constants.TREATMENT_START_NOTIFICATION
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
import au.edu.unsw.business.studysync.support.TimeUtils
import au.edu.unsw.business.studysync.support.TimeUtils.nowLD
import au.edu.unsw.business.studysync.support.TimeUtils.nowZDT
import au.edu.unsw.business.studysync.support.UsageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration

class DailySchedulerWorker(private val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("App/DailySchedulerWorker", "begin")

        val recordRequest = RecordWorker.createRequest()

        val subjectSettings = withContext(Dispatchers.Main) {
            SubjectSettings(context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE))
        }
        val workManager = WorkManager.getInstance(context)

        if (UsageUtils.hasUsageStatsPermission(context)) {
            val firstStep = workManager.beginUniqueWork(
                RECORD_AND_SUBMIT_WORK,
                ExistingWorkPolicy.REPLACE,
                recordRequest
            )

            val allWork = if (subjectSettings.identified.value!!) {
                val submitRequest = SubmitWorker.createRequest()
                firstStep.then(submitRequest)
            } else {
                firstStep
            }

            allWork.enqueue()
        }

        Log.d("App/DailySchedulerWorker", "work enqueued (record${ if (subjectSettings.identified.value!!) ", submit" else "" })")

        // Trigger the daily scheduler worker to check for updated study dates
        workManager.enqueueUniqueWork(DAILY_CHECK_STUDY_DATES, ExistingWorkPolicy.REPLACE, UpdateStudyDatesWorker.createRequest())

        val today = nowLD()
        val tomorrow = today.plusDays(1)

        if (!tomorrow.isAfter(TimeUtils.studyDates.overDate)) {
            workManager.enqueueUniqueWork(
                DAILY_SCHEDULER_BOUNCE_WORK,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<DailySchedulerBounceWorker>().build()
            )

            Log.d("App/DailySchedulerWorker", "DailySchedulerBounceWorker enqueued")
        }

        if (subjectSettings.identified.value!! && (today.isEqual(TimeUtils.studyDates.treatmentDate) || today.isEqual(TimeUtils.studyDates.overDate))) {
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)

            val (title, text, notificationId) = if (today.isEqual(TimeUtils.studyDates.treatmentDate)) {
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

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return Result.success()

            }
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }

        Log.d("App/DailySchedulerWorker", "success")
        return Result.success()
    }

    companion object {
        fun createRequestForNext0001(): OneTimeWorkRequest {
            val now = nowZDT()
            val next0001 = nowLD().plusDays(1).atStartOfDay(ZONE_ID).plusMinutes(0)
            // val next0001 = now.plusSeconds(15)
            Log.d("App/DailySchedulerWorker", now.toString())
            Log.d("App/DailySchedulerWorker", next0001.toString())
            Log.d("App/DailySchedulerWorker", Duration.between(now, next0001).toString())

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