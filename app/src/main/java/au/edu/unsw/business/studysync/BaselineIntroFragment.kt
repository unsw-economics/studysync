package au.edu.unsw.business.studysync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import au.edu.unsw.business.studysync.constants.Constants.DEBUG_DATA
import au.edu.unsw.business.studysync.databinding.FragmentBaselineIntroBinding
import au.edu.unsw.business.studysync.logic.TimeUtils.getStudyPeriodAndDay
import au.edu.unsw.business.studysync.logic.TimeUtils.humanizeTime
import au.edu.unsw.business.studysync.logic.TimeUtils.midnight
import au.edu.unsw.business.studysync.logic.TimeUtils.now
import au.edu.unsw.business.studysync.logic.TimeUtils.toMilliseconds
import au.edu.unsw.business.studysync.network.ReportPayload
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer.computeUsage
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer.prepareReports
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.time.LocalDate
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BaselineIntroFragment: Fragment() {

    private var _binding: FragmentBaselineIntroBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBaselineIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = vm

        binding.clearDataButton.setOnClickListener {
            lifecycleScope.launch {
                vm.clearData()
            }
        }

        binding.submitReportButton.setOnClickListener {
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    val (period, day) = getStudyPeriodAndDay(LocalDate.now())

                    val reports = prepareReports(
                        computeUsage(
                            requireContext(),
                            midnight(),
                            now()
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

        binding.recordNewUsagesButton.setOnClickListener {
            var date = vm.lastRecorded.value!!
            val today = LocalDate.now()

            val usagesList: MutableList<Pair<String, Map<String, Long>>> = LinkedList()

            while (date.isBefore(today)) {
                val nextDate = date.plusDays(1)
                val usage = computeUsage(requireContext(), toMilliseconds(date), toMilliseconds(nextDate))

                usagesList.add(Pair(date.toString(), usage))
                date = nextDate
            }

            // commit to database instead of displaying to screen
            // then update lastRecorded

            var text = ""
            for ((d, usage) in usagesList) {
                text += "Usage data for $d:\n"

                for ((app, seconds) in usage) {
                    text += "$app ${humanizeTime(seconds)}\n"
                }

                text += "\n"

            }

            val intent = Intent(activity, DebugActivity::class.java).apply {
                putExtra(DEBUG_DATA, text)
            }

            startActivity(intent)
        }

        binding.displayAllUsagesButton.setOnClickListener {
            lifecycleScope.launch {
                val reports = vm.getRecordedReports()

                var text = ""

                for (report in reports) {
                    text += "${report.day} ${report.period} ${report.json}\n"
                }

                val intent = Intent(activity, DebugActivity::class.java).apply {
                    putExtra(DEBUG_DATA, text)
                }

                startActivity(intent)
            }
        }

        vm.identified.observe(viewLifecycleOwner) {
            if (!it) {
                findNavController().navigate(R.id.action_baseline_intro_to_login)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}