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
import au.edu.unsw.business.studysync.database.DbAppReport
import au.edu.unsw.business.studysync.database.DbReport
import au.edu.unsw.business.studysync.databinding.FragmentBaselineIntroBinding
import au.edu.unsw.business.studysync.development.DevUtils
import au.edu.unsw.business.studysync.development.DevUtils.printList
import au.edu.unsw.business.studysync.logic.TimeUtils.getStudyPeriodAndDay
import au.edu.unsw.business.studysync.logic.TimeUtils.humanizeTime
import au.edu.unsw.business.studysync.logic.TimeUtils.midnight
import au.edu.unsw.business.studysync.logic.TimeUtils.now
import au.edu.unsw.business.studysync.logic.TimeUtils.toMilliseconds
import au.edu.unsw.business.studysync.network.ReportPayload
import au.edu.unsw.business.studysync.network.ServerAppReport
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer.computeUsage
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer.computeUsageOriginal
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.lang.Error
import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap

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
                val unsyncedAppReports = withContext(Dispatchers.IO) {
                    vm.getUnsyncedAppReports()
                }

                val subjectId = vm.subjectId.value!!

                val payloadMap: MutableMap<Pair<String, Int>, ReportPayload> = HashMap()

                for (appReport in unsyncedAppReports) {
                    val period = appReport.period
                    val day = appReport.day

                    val periodDay = Pair(appReport.period, appReport.day)

                    if (!payloadMap.contains(periodDay)) {
                        payloadMap[periodDay] = ReportPayload(subjectId, period, day, LinkedList())
                    }

                    val serverAppReport =
                        ServerAppReport(appReport.applicationName, appReport.usageSeconds)
                    (payloadMap[periodDay]!!.reports as MutableList).add(serverAppReport)
                }

                val authToken = vm.authToken.value!!

                withContext(Dispatchers.IO) {
                    for ((_, payload) in payloadMap) {
                        try {
                            Log.d("MainActivity", "Submitting $payload")
                            val response = SyncApi.service.submitReport(authToken, payload)

                            Log.d("MainActivity", "Submitted")
                            vm.markReportSynced(payload.period, payload.day)
                        } catch (e: HttpException) {
                            val errorBody = e.response()?.errorBody()?.source().toString()
                            Log.d("MainActivity", "Error: ${e.message}; $errorBody")
                            // TODO handle exception properly
                        }
                    }
                }
            }
        }

        binding.recordNewUsagesButton.setOnClickListener {
            var date = vm.lastRecorded.value!!
            val today = LocalDate.now()

            val reports: MutableList<DbReport> = LinkedList()
            val appReports: MutableList<DbAppReport> = LinkedList()

            while (date.isBefore(today)) {
                val nextDate = date.plusDays(1)
                val usage = computeUsage(requireContext(), toMilliseconds(date), toMilliseconds(nextDate))
                val (period, day) = getStudyPeriodAndDay(date)

                reports.add(
                    DbReport(
                        period,
                        day
                    )
                )

                for ((appName, usage_ms) in usage) {
                    Log.d("MainActivity", "$period $day $appName ${usage_ms / 1000}")
                    appReports.add(DbAppReport(period, day, appName, usage_ms / 1000))
                }

                date = nextDate
            }

            MainScope().launch {
                vm.insertMultipleDayReports(reports, appReports)
                vm.setLastRecorded(today)

                val text = printList(reports) + "\n" + printList(appReports)

                val intent = Intent(activity, DebugActivity::class.java).apply {
                    putExtra(DEBUG_DATA, text)
                }

                startActivity(intent)
            }
        }

        binding.displayRecordedUsagesButton.setOnClickListener {
            lifecycleScope.launch {
                val appReports = vm.getAllAppReports()
                val text = printList(appReports.map {
                    "${it.period} ${it.day} ${humanizeTime(it.usageSeconds * 1000)}\n${it.applicationName}"
                })

                val intent = Intent(activity, DebugActivity::class.java).apply {
                    putExtra(DEBUG_DATA, text)
                }

                startActivity(intent)
            }
        }

        binding.displayTodayUsagesButton.setOnClickListener {
            lifecycleScope.launch {
                val todayUsages = computeUsage(requireContext(), midnight(), now())
                val text = printList(todayUsages.toList().map {
                    "${it.first} ${humanizeTime(it.second)}"
                })
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