package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.edu.unsw.business.studysync.database.AppDatabase

class TreatmentViewModelFactory(
    private val application: StudySyncApplication
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TreatmentViewModel(application) as T
    }
}