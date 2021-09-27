package au.edu.unsw.business.studysync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    private val _loginEnabled = MutableLiveData<Boolean>()
    val loginEnabled get(): LiveData<Boolean> = _loginEnabled

    init {
        _loginEnabled.value = true
    }

    fun disableLogin() {
        _loginEnabled.value = false
    }

    fun enableLogin() {
        _loginEnabled.value = true
    }
}