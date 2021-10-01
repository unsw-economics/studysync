package au.edu.unsw.business.studysync.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import au.edu.unsw.business.studysync.DebugActivity
import au.edu.unsw.business.studysync.MainActivity
import au.edu.unsw.business.studysync.MainViewModel
import au.edu.unsw.business.studysync.constants.Constants.DEBUG_DATA
import au.edu.unsw.business.studysync.database.DbAppReport
import au.edu.unsw.business.studysync.database.DbReport
import au.edu.unsw.business.studysync.databinding.FragmentBaselineIntroBinding
import au.edu.unsw.business.studysync.development.DevUtils.printList
import au.edu.unsw.business.studysync.logic.TimeUtils.getStudyPeriodAndDay
import au.edu.unsw.business.studysync.logic.TimeUtils.humanizeTimeHms
import au.edu.unsw.business.studysync.logic.TimeUtils.midnight
import au.edu.unsw.business.studysync.logic.TimeUtils.now
import au.edu.unsw.business.studysync.logic.TimeUtils.toMilliseconds
import au.edu.unsw.business.studysync.network.ReportPayload
import au.edu.unsw.business.studysync.network.ServerAppReport
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer.computeUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.time.Duration
import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap

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
                (activity as MainActivity).navigate()
            }
        }

        binding.submitReportButton.setOnClickListener {
            MainScope().launch {
                val unsyncedAppReports = withContext(Dispatchers.IO) {
                    vm.getUnsyncedAppReports()
                }

                val subjectId = vm.subjectSettings.subjectId.value!!

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

                val authToken = vm.subjectSettings.authToken.value!!

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
            var date = vm.subjectSettings.lastRecorded.value!!
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
                    "${it.period} ${it.day} ${humanizeTimeHms(Duration.ofSeconds(it.usageSeconds))}\n${it.applicationName}"
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
                    "${it.first} ${humanizeTimeHms(Duration.ofMillis(it.second))}"
                })
                val intent = Intent(activity, DebugActivity::class.java).apply {
                    putExtra(DEBUG_DATA, text)
                }

                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}