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
import au.edu.unsw.business.studysync.viewmodels.LoginViewModel
import au.edu.unsw.business.studysync.viewmodels.MainViewModel
import au.edu.unsw.business.studysync.databinding.FragmentLoginBinding
import au.edu.unsw.business.studysync.network.RobustFetchTestParameters
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.support.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

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
            val subjectId = binding.subjectIdField.text.toString()
            loginVm.disableLogin()

            lifecycleScope.launch {
                try {
                    val idResponse = SyncApi.service.identify(subjectId)

                    if (idResponse.message != null) throw Exception(idResponse.message)

                    val idData = idResponse.data!!

                    if (TimeUtils.getTodayPeriod() == "BASELINE") {
                        vm.identify(subjectId, idData.authToken)
                    } else {
                        val glResult = RobustFetchTestParameters.fetch(idData.authToken, subjectId)

                        if (glResult.isFailure) {
                            throw glResult.exceptionOrNull()!!
                        }

                        val (testGroup, treatmentLimit) = glResult.getOrNull()!!
                        vm.identifyFully(subjectId, idData.authToken, testGroup, treatmentLimit)
                    }
                } catch (e: Exception) {
                    Log.d("MainActivity", "Error: ${e.message}")
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