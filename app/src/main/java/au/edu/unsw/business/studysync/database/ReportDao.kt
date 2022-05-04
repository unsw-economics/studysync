package au.edu.unsw.business.studysync.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReportDao {
    @Query("select AR.* from reports R join app_reports AR on (R.period = AR.period and R.day = AR.day)")
    fun getAllAppReports(): List<DbAppReport>

    @Query("select AR.* from reports R join app_reports AR on (R.period = AR.period and R.day = AR.day) where R.period = 'experiment'")
    fun getExperimentAppReports(): List<DbAppReport>

    @Query("select AR.* from reports R join app_reports AR on (R.period = AR.period and R.day = AR.day) where R.synced = 0")
    fun getUnsyncedAppReports(): List<DbAppReport>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultipleDayReports(reports: List<DbReport>, appReports: List<DbAppReport>)

    @Query("update reports set synced = 1 where period = :period and day = :day")
    fun markReportSynced(period: String, day: Int)
}