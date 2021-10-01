package au.edu.unsw.business.studysync

import android.app.Application
import android.content.Context
import au.edu.unsw.business.studysync.database.AppDatabase

class StudySyncApplication: Application() {
    val preferences by lazy {
        getSharedPreferences("studysync-config", Context.MODE_PRIVATE)
    }

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val subjectSettings: SubjectSettings by lazy {
        SubjectSettings(preferences)
    }
}