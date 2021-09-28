package au.edu.unsw.business.studysync

import android.app.Application
import au.edu.unsw.business.studysync.database.AppDatabase

class StudySyncApplication: Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}