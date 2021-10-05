package au.edu.unsw.business.studysync.network

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import au.edu.unsw.business.studysync.constants.Constants.FETCH_TEST_PARAMS_WORK
import au.edu.unsw.business.studysync.workers.FetchTestParametersWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.Exception

object RobustFetchTestParameters {
    suspend fun fetch(authToken: String, subjectId: String): Result<Pair<Int, Int>> {
        Log.d("App/RobustFetchTestParameters (fetch)", "begin")

        try {
            val result = withContext(Dispatchers.IO) {
                SyncApi.service.getGroupAndLimit(authToken, subjectId)
            }

            val data = result.data!!

            if (data.testGroup == null) return Result.failure(Exception("No test group found for subject. Please try again later."))

            if (data.treatmentLimit == null) return Result.failure(Exception("No treatment limit found for subject. Please try again later."))

            Log.d("App/RobustFetchTestParameters (fetch)", "success")
            return Result.success(Pair(data.testGroup, data.treatmentLimit))
        } catch (ex: Exception) {
            Log.d("App/RobustFetchTestParameters (fetch)", "failure")
            return Result.failure(ex)
        }
    }

    suspend fun fetchOrScheduleRetry(context: Context, authToken: String, subjectId: String): Result<Pair<Int, Int>> {
        Log.d("App/RobustFetchTestParameters (fetchOrScheduleRetry)", "begin")

        val result = fetch(authToken, subjectId)

        if (result.isSuccess) {
            Log.d("App/RobustFetchTestParameters (fetchOrScheduleRetry)", "success")
            return result
        }

        val request = FetchTestParametersWorker.createRequest()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(FETCH_TEST_PARAMS_WORK, ExistingWorkPolicy.REPLACE, request)

        Log.d("App/RobustFetchTestParameters (fetchOrScheduleRetry)", "retry scheduled")
        return Result.failure(result.exceptionOrNull()!!)
    }
}