package au.edu.unsw.business.studysync.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DailyReportDao {
    @Query("select * from daily_reports")
    suspend fun getRecordedReports(): List<DailyReport>
}