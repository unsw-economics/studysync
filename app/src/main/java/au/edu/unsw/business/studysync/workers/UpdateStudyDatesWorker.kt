package au.edu.unsw.business.studysync.workers

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.work.*
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.network.SyncApi
import org.acra.ACRA
import java.time.Duration

class UpdateStudyDatesWorker(private val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            val preferences = context.getSharedPreferences("studysync-config", Context.MODE_PRIVATE)

            val result = SyncApi.service.getDates(preferences.getString("auth-token", null), preferences.getString("subject-id", null))

            if (result.message != null) throw Exception(result.message)

            val studyDates = result.data!!

            if (studyDates.baselineDate != null &&
                studyDates.treatmentDate != null &&
                studyDates.endlineDate != null &&
                studyDates.overDate != null) {
                preferences.edit {
                    putString("baseline-date", studyDates.baselineDate)
                    putString("treatment-date", studyDates.treatmentDate)
                    putString("endline-date", studyDates.endlineDate)
                    putString("over-date", studyDates.overDate)
                }
            }
            return Result.success()
        } catch (ex: Exception) {
            ACRA.errorReporter.handleSilentException(ex)
            Log.d("UpdateStudyDatesWorker", ex.message!!)
            return Result.retry()
        }
    }

    companion object {
        fun createRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<UpdateStudyDatesWorker>()
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofHours(1))
                .build()
        }
    }
}