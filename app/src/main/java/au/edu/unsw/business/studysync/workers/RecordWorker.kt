package au.edu.unsw.business.studysync.workers

import android.content.Context
import android.os.UserManager
import android.util.Log
import androidx.work.*
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.constants.Constants.PREFERENCES_NAME
import au.edu.unsw.business.studysync.support.UsageUtils
import au.edu.unsw.business.studysync.usage.UsageDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.acra.ACRA
import java.time.Duration

class RecordWorker(private val context: Context, private val params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("App/RecordWorker", "begin")

        if (params.runAttemptCount > 30) {
            Log.d("App/RecordWorker", "failing after 30 attempts")
            return Result.failure()
        }

        return try {
            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
            val isPermitted = UsageUtils.hasUsageStatsPermission(context)

            return if (userManager.isUserUnlocked && isPermitted) {
                val usageDriver = withContext(Dispatchers.Main) {
                    UsageDriver(context, SubjectSettings(context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)))
                }

                withContext(Dispatchers.IO) {
                    usageDriver.recordNewUsages()
                }

                Log.d("App/RecordWorker", "success")
                Result.success()
            } else {
                Log.d("App/RecordWorker", "retry")
                Result.retry()
            }
        } catch (ex: Exception) {
            Log.d("App/RecordWorker", "retry -- record error: ${ex.message}")
            ACRA.errorReporter.handleSilentException(ex)
            Result.retry()
        }
    }

    companion object {
        fun createRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RecordWorker>()
                .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(1))
                .build()
        }
    }
}