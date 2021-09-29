package au.edu.unsw.business.studysync.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DbReport::class, DbAppReport::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "studysync-db"
                ).fallbackToDestructiveMigration().build()

                instance
            }
        }
    }

    abstract fun reportDao(): ReportDao
}