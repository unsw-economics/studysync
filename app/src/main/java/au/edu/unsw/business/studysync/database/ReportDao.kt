package au.edu.unsw.business.studysync.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReportDao {
    @Query("select AR.* from reports R join app_reports AR on (R.period = AR.period and R.day = AR.day)")
    suspend fun getAllAppReports(): List<DbAppReport>

    @Query("select AR.* from reports R join app_reports AR on (R.period = AR.period and R.day = AR.day) where R.period = 'experiment'")
    suspend fun getExperimentAppReports(): List<DbAppReport>

    @Query("select AR.* from reports R join app_reports AR on (R.period = AR.period and R.day = AR.day) where R.synced = false")
    suspend fun getUnsyncedAppReports(): List<DbAppReport>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultipleDayReports(reports: List<DbReport>, appReports: List<DbAppReport>)

    @Query("update reports set synced = true where period = :period and day = :day")
    suspend fun markReportSynced(period: String, day: Int)
}