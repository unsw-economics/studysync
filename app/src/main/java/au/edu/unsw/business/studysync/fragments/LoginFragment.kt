package au.edu.unsw.business.studysync.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_BASELINE
import au.edu.unsw.business.studysync.databinding.FragmentLoginBinding
import au.edu.unsw.business.studysync.network.RobustFetchTestParameters
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.support.TimeUtils
import au.edu.unsw.business.studysync.viewmodels.LoginViewModel
import au.edu.unsw.business.studysync.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.acra.ACRA

fun hashStringTo8Digit(input: String): Int {
    var hash = 0

    for (char in input) {
        hash += char.toInt()
        hash += (hash shl 10)
        hash = hash xor (hash shr 6)
    }

    hash += (hash shl 3)
    hash = hash xor (hash shr 11)
    hash += (hash shl 15)

    // Ensure the result lies in the range [0, 89999999].
    hash = Math.abs(hash % 90000000)

    // Adjust to ensure it's an 8-digit number without leading zeros: [10000000, 99999999].
    return hash + 10000000
}

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()
    private val loginVm: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.identifyButton.setOnClickListener {
            val email = binding.subjectIdField.text.toString()
            val subjectId = hashStringTo8Digit(email).toString()
            loginVm.disableLogin()

            lifecycleScope.launch {
                try {
                    val idResponse = SyncApi.service.identify(email)

                    if (idResponse.message != null) throw Exception(idResponse.message)

                    val idData = idResponse.data!!

                    // Retrieve the study dates from the server
                    val studyDatesResponse = SyncApi.service.getDates(idData.authToken, subjectId)

                    if (studyDatesResponse.message != null) throw Exception(studyDatesResponse.message)

                    val studyDates = studyDatesResponse.data!!

                    // If no dates are null then update the study dates in the time utils
                    if (studyDates.baselineDate != null &&
                        studyDates.treatmentDate != null &&
                        studyDates.endlineDate != null &&
                        studyDates.overDate != null) {
                        vm.updateDates(studyDates.baselineDate, studyDates.treatmentDate, studyDates.endlineDate, studyDates.overDate)
                    }

                    if (TimeUtils.getTodayPeriod() == PERIOD_BASELINE) {
                        vm.identify(subjectId, idData.authToken)
                    } else {
                        val glResult = RobustFetchTestParameters.fetch(idData.authToken, subjectId)

                        if (glResult.isFailure) {
                            throw glResult.exceptionOrNull()!!
                        }

                        val (testGroup, treatmentIntensity, treatmentLimit) = glResult.getOrNull()!!
                        vm.identifyFully(subjectId, idData.authToken, testGroup, treatmentIntensity, treatmentLimit)
                    }
                } catch (e: Exception) {
                    Log.d("App/LoginFragment", "login error: ${e.message}")
                    ACRA.errorReporter.handleSilentException(e)
                    // to avoid flashing on error
                    delay(300)
                    loginVm.enableLogin()
                }
            }
        }

        loginVm.loginEnabled.observe(viewLifecycleOwner) {
            binding.identifyButton.isEnabled = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}