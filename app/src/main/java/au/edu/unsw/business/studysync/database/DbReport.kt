package au.edu.unsw.business.studysync.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reports",
    indices = [
        Index(value = ["period", "day"], unique = true)
    ]
)
data class DbReport(
    @NonNull
    val period: String,
    @NonNull
    val day: Int,
    @NonNull
    val synced: Boolean = false
) {
    @PrimaryKey(autoGenerate = true) @NonNull
    var id: Int = 0
}

