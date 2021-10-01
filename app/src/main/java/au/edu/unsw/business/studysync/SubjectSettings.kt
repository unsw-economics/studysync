package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import au.edu.unsw.business.studysync.constants.Constants
import au.edu.unsw.business.studysync.constants.Environment
import au.edu.unsw.business.studysync.usage.UsageStatsNegotiator
import java.time.Duration
import java.time.LocalDate

class SubjectSettings(
    private val preferences: SharedPreferences
) {

    private val _identified = MutableLiveData<Boolean>()
    val identified get(): LiveData<Boolean> = _identified

    private val _subjectId = MutableLiveData<String?>()
    val subjectId get(): LiveData<String?> = _subjectId

    private val _authToken = MutableLiveData<String?>()
    val authToken get(): LiveData<String?> = _authToken

    private val _lastRecorded = MutableLiveData<LocalDate>()
    val lastRecorded get(): LiveData<LocalDate> = _lastRecorded

    private val _treatmentGroup = MutableLiveData<Int>()
    val treatmentGroup get(): LiveData<Int> = _treatmentGroup

    private val _treatmentDebriefed = MutableLiveData<Boolean>()
    val treatmentDebriefed get(): LiveData<Boolean> = _treatmentDebriefed

    private val _treatmentLimit = MutableLiveData<Duration>()
    val treatmentLimit get(): LiveData<Duration> = _treatmentLimit

    init {
        _identified.value = preferences.getBoolean("identified", false)
        _subjectId.value = preferences.getString("subject-id", null)
        _authToken.value = preferences.getString("auth-token", null)
        _lastRecorded.value = LocalDate.parse(
            preferences.getString(
                "last-recorded",
                Environment.BASELINE_START_DATE_STRING
            )
        )
        _treatmentGroup.value = preferences.getInt("treatment-group", 3)
        _treatmentDebriefed.value = preferences.getBoolean("treatment-debriefed", false)
        _treatmentLimit.value = Duration.ofSeconds(preferences.getLong("treatment-limit", Duration.ofHours(2).seconds))
    }

    fun identify(subjectId: String, authToken: String) {
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

    fun clearData() {
        preferences.edit {
            clear()
            commit()
        }

        _identified.value = false
        _subjectId.value = null
        _authToken.value = null
        _lastRecorded.value = Environment.BASELINE_START_DATE
        _treatmentGroup.value = Constants.GROUP_UNASSIGNED
        _treatmentDebriefed.value = false
        _treatmentLimit.value = Duration.ofSeconds(0)
    }

    fun setLastRecorded(date: LocalDate) {
        preferences.edit {
            putString("last-recorded", date.toString())
        }

        _lastRecorded.value = date
    }

    fun completeDebrief() {
        preferences.edit {
            putBoolean("treatment-debriefed", true)
        }

        _treatmentDebriefed.value = true
    }
}