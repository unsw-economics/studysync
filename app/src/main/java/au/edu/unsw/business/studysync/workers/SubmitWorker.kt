package au.edu.unsw.business.studysync.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.constants.Constants
import au.edu.unsw.business.studysync.usage.UsageDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.acra.ACRA
import java.time.Duration

class SubmitWorker(private val context: Context, private val params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("App/SubmitWorker", "begin")

        if (params.runAttemptCount > 20) {
            Log.d("App/SubmitWorker", "failing after 20 attempts")
            return Result.failure()
        }

        return try {
            val usageDriver = withContext(Dispatchers.Main) {
                UsageDriver(context, SubjectSettings(context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)))
            }

            usageDriver.submitUnsyncedReports()

            Log.d("App/SubmitWorker", "success")
            Result.success()
        } catch (ex: Exception) {
            ACRA.errorReporter.handleSilentException(ex)
            Log.d("App/SubmitWorker", "retry")
            Result.retry()
        }
    }

    companion object {
        private val NETWORK_CONSTRAINT = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun createRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SubmitWorker>()
                .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(1))
                .setConstraints(NETWORK_CONSTRAINT)
                .build()
        }
    }
}