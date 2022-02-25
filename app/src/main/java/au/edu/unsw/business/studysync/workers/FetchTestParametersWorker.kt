package au.edu.unsw.business.studysync.workers

import android.content.Context
import androidx.work.*
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.network.RobustFetchTestParameters
import org.acra.ACRA
import java.time.Duration

class FetchTestParametersWorker(private val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            val preferences = context.getSharedPreferences("studysync-config", Context.MODE_PRIVATE)
            val subjectSettings = SubjectSettings(preferences)

            val result = RobustFetchTestParameters.fetch(subjectSettings.authToken.value!!, subjectSettings.subjectId.value!!)

            if (result.isFailure) {
                throw result.exceptionOrNull()!!
            }

            val (testGroup, treatmentIntensity, treatmentLimit) = result.getOrNull()!!

            subjectSettings.setTestParameters(testGroup, treatmentIntensity, treatmentLimit)

            return Result.success()
        } catch (ex: Exception) {
            ACRA.errorReporter.handleSilentException(ex)
            return Result.retry()
        }
    }

    companion object {
        fun createRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<FetchTestParametersWorker>()
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofHours(1))
                .build()
        }
    }
}