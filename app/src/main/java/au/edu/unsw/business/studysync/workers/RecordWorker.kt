package au.edu.unsw.business.studysync.workers

import android.content.Context
import android.os.UserManager
import android.util.Log
import androidx.work.*
import au.edu.unsw.business.studysync.SubjectSettings
import au.edu.unsw.business.studysync.constants.Constants.PREFERENCES_NAME
import au.edu.unsw.business.studysync.usage.UsageDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration

class RecordWorker(private val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("MainRecord", "record new usages")

            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager

            return if (userManager.isUserUnlocked) {
                val usageDriver = withContext(Dispatchers.Main) {
                    UsageDriver(context, SubjectSettings(context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)))
                }

                withContext(Dispatchers.IO) {
                    usageDriver.recordNewUsages()
                }

                Result.success()
            } else {
                Result.retry()
            }
        } catch (ex: Exception) {
            Log.d("Main", "Error: ${ex.message}")
            Result.retry()
        }
    }

    companion object {
        fun createRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RecordWorker>()
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofSeconds(10))
                .build()
        }
    }

}