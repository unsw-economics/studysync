package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.edu.unsw.business.studysync.database.DailyReportDao

class TreatmentViewModelFactory(
    private val preferences: SharedPreferences,
    private val dailyReportDao: DailyReportDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TreatmentViewModel(preferences, dailyReportDao) as T
    }
}