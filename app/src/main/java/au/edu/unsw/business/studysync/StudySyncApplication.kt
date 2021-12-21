package au.edu.unsw.business.studysync

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import au.edu.unsw.business.studysync.constants.Constants.PREFERENCES_NAME
import au.edu.unsw.business.studysync.constants.Constants.STUDY_PHASE_CHANNEL
import au.edu.unsw.business.studysync.database.AppDatabase
import au.edu.unsw.business.studysync.usage.UsageDriver

class StudySyncApplication: Application() {
    val preferences by lazy {
        getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val subjectSettings: SubjectSettings by lazy {
        SubjectSettings(preferences)
    }

    val usageDriver: UsageDriver by lazy {
        UsageDriver(applicationContext, subjectSettings)
    }

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(STUDY_PHASE_CHANNEL, "Study Phase", IMPORTANCE_HIGH)
        channel.lockscreenVisibility = VISIBILITY_PUBLIC
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}