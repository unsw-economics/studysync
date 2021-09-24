package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: SharedPreferences): ViewModel() {
    private val _identified = MutableLiveData<Boolean>()
    val identified get(): LiveData<Boolean> = _identified

    private val _subjectId = MutableLiveData<String?>()
    val subjectId get(): LiveData<String?> = _subjectId

    private val _authToken = MutableLiveData<String?>()
    val authToken get(): LiveData<String?> = _authToken

    init {
        _identified.value = preferences.getBoolean("identified", false)
        _subjectId.value = preferences.getString("subject-id", null)
        _authToken.value = preferences.getString("auth-token", null)
    }

    fun identify(subjectId: String, authToken: String) {
        viewModelScope.launch {
            preferences.edit {
                putBoolean("identified", true)
                putString("subject-id", subjectId)
                putString("auth-token", authToken)
                commit()
            }

            _identified.value = true
            _subjectId.value = subjectId
            _authToken.value = authToken
        }
    }

    fun clearIdentity() {
        viewModelScope.launch {
            preferences.edit {
                putBoolean("identified", false)
                putString("subject-id", null)
                putString("auth-token", null)
                commit()
            }

            _identified.value = false
            _subjectId.value = null
            _authToken.value = null
        }
    }
}