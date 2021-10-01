package au.edu.unsw.business.studysync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TreatmentViewModelFactory(
    private val application: StudySyncApplication
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TreatmentViewModel(application) as T
    }
}