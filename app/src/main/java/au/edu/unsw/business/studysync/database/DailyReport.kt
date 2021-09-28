package au.edu.unsw.business.studysync.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reports")
data class DailyReport(
    @PrimaryKey @NonNull
    val id: Int,
    @NonNull
    val period: String,
    @NonNull
    val day: Int,
    @NonNull
    val json: String,
    @NonNull
    val synced: Boolean
)
