package au.edu.unsw.business.studysync

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import au.edu.unsw.business.studysync.constants.Environment.DAILY_REPORT_WORKER_TAG
import au.edu.unsw.business.studysync.constants.Environment.NETWORK_CONSTRAINT
import au.edu.unsw.business.studysync.network.SyncApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ReportDailyWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        Log.d("MainActivity", "Inside do work")
        withContext(Dispatchers.IO) {
            val response = SyncApi.service.identify("aaaaaa000000")
            Log.d("MainActivity", "Work manager called")

        }
        return Result.success()
    }


}