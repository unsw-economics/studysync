package au.edu.unsw.business.studysync.database

import androidx.annotation.NonNull
import androidx.room.*

@Entity(
    tableName = "app_reports",
    indices = [
        Index(value = ["period", "day", "application_name"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbReport::class,
            parentColumns = ["period", "day"],
            childColumns = ["period", "day"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbAppReport(
    @NonNull
    val period: String,
    @NonNull
    val day: Int,
    @ColumnInfo(name = "application_name") @NonNull
    val applicationName: String,
    @ColumnInfo(name = "usage_seconds") @NonNull
    val usageSeconds: Long
) {
    @PrimaryKey(autoGenerate = true) @NonNull
    var id: Int = 0
}
