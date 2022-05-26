package au.edu.unsw.business.studysync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import au.edu.unsw.business.studysync.constants.Constants
import au.edu.unsw.business.studysync.workers.DailySchedulerWorker
import au.edu.unsw.business.studysync.workers.UpdateStudyDatesWorker
import au.edu.unsw.business.studysync.workers.UsageWorker

class WorkManagerStartReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(Constants.DAILY_SCHEDULER_WORK, ExistingWorkPolicy.REPLACE, DailySchedulerWorker.createRequestForNext0001())
        workManager.enqueueUniquePeriodicWork(Constants.PERODIC_SUBMIT_USAGE_WORK, ExistingPeriodicWorkPolicy.REPLACE, UsageWorker.createRequest())
        workManager.enqueueUniquePeriodicWork(Constants.PERODIC_UPDATE_DATES_WORK, ExistingPeriodicWorkPolicy.REPLACE, UpdateStudyDatesWorker.createPerodicRequest())
    }
}