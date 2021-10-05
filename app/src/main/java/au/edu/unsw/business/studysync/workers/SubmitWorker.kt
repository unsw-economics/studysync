package au.edu.unsw.business.studysync.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import java.time.Duration

class SubmitWorker(private val context: Context, private val params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("MainRecord", "submit unsynced reports")
            Result.success()
        } catch (ex: Exception) {
            Result.retry()
        }
    }

    companion object {
        private val NETWORK_CONSTRAINT = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun createRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SubmitWorker>()
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofMinutes(1))
                .setConstraints(NETWORK_CONSTRAINT)
                .build()
        }
    }
}