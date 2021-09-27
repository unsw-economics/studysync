package au.edu.unsw.business.studysync

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE
import au.edu.unsw.business.studysync.databinding.FragmentDebriefBinding
import au.edu.unsw.business.studysync.logic.TimeUtils.getStudyPeriodAndDay
import au.edu.unsw.business.studysync.logic.TimeUtils.getToday
import au.edu.unsw.business.studysync.network.BasicApiResponse
import au.edu.unsw.business.studysync.network.ReportPayload
import au.edu.unsw.business.studysync.network.SyncApi
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.lang.Exception
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DebriefFragment : Fragment() {

    private var _binding: FragmentDebriefBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDebriefBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = vm

        binding.clearIdentityButton.setOnClickListener {
            lifecycleScope.launch {
                vm.clearIdentity()
            }
        }

        binding.submitReportButton.setOnClickListener {
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    val (period, day) = getStudyPeriodAndDay(getToday())

                    val reports = UsageStatsNegotiator.prepareReports(
                        UsageStatsNegotiator.computeUsagesSerial(
                            requireContext(),
                            getToday().timeInMillis,
                            System.currentTimeMillis()
                        )
                    )

                    val reportPayload = ReportPayload(vm.subjectId.value!!, period, day, reports)
                    val authToken = vm.authToken.value!!

                    try {
                        Log.d("MainActivity", "Submitting $reportPayload")
                        val response = SyncApi.service.submitReport(authToken, reportPayload)

                        if (response.message != null) {
                            throw Exception(response.message)
                        }

                        Log.d("MainActivity", "Successfully reported")
                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.source().toString()
                        Log.d("MainActivity", "Error: ${e.message}; $errorBody")
                        TODO("handle exception properly")
                    }
                }
            }
        }

        vm.identified.observe(viewLifecycleOwner) {
            if (!it) {
                findNavController().navigate(R.id.action_debrief_to_login)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}