package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.edu.unsw.business.studysync.database.AppDatabase

class MainViewModelFactory(
    private val preferences: SharedPreferences,
    private val database: AppDatabase
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(preferences, database) as T
    }
}