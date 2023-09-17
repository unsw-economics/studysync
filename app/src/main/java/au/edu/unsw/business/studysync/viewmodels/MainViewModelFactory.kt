package au.edu.unsw.business.studysync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.edu.unsw.business.studysync.StudySyncApplication

class MainViewModelFactory(
    private val application: StudySyncApplication
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}