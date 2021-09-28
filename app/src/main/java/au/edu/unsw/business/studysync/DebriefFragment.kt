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
import au.edu.unsw.business.studysync.UsageStatsNegotiator.computeUsagesSerial
import au.edu.unsw.business.studysync.UsageStatsNegotiator.getEventsJson
import au.edu.unsw.business.studysync.constants.Constants.DEBUG_DATA
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
import au.edu.unsw.business.studysync.databinding.FragmentDebriefBinding
import au.edu.unsw.business.studysync.logic.TimeUtils.humanizeTime
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*
import kotlin.math.min

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

        binding.clearDataButton.setOnClickListener {
            lifecycleScope.launch {
                vm.clearData()
            }
        }

        binding.submitReportButton.setOnClickListener {
            val json = UsageStatsNegotiator.getEventsJson(requireContext(), LocalDate.now().atStartOfDay(
                ZONE_ID).toInstant().toEpochMilli(), ZonedDateTime.now().toInstant().toEpochMilli())

            val str = json.toString(2)

            val length = str.length

            for (i in 0..(length / 1000)) {
                Log.d("MainActivity", str.subSequence(i * 1000, min(i * 1000 + 1000, length)).toString())
            }

        }

        /*
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
        */

        binding.recordNewUsagesButton.setOnClickListener {
            /*
            val now = ZonedDateTime.now()
            val midnight = now.toLocalDate().atStartOfDay(ZONE_ID)
            val (period, day) = getStudyPeriodAndDay(LocalDate.now())

            Log.d("MainActivity", TimeUtils.humanizeTime(ChronoUnit.MILLIS.between(midnight, now)))
            Log.d("MainActivity", "$period $day")

            Log.d("MainActivity", getTodayUsageJson(requireContext()).toString())
            */

            var date = vm.lastRecorded.value!!
            val today = LocalDate.now()

            val usagesList: MutableList<Pair<String, Map<String, Long>>> = LinkedList()

            while (date.isBefore(today)) {
                val nextDate = date.plusDays(1)
                val usage = computeUsagesSerial(
                    requireContext(),
                    date.atStartOfDay(ZONE_ID).toInstant().toEpochMilli(),
                    nextDate.atStartOfDay(ZONE_ID).toInstant().toEpochMilli()
                )

                Log.d("MainActivity", date.toString())

                Log.d("MainActivity", getEventsJson(
                    requireContext(),
                    date.atStartOfDay(ZONE_ID).toInstant().toEpochMilli(),
                    nextDate.atStartOfDay(ZONE_ID).toInstant().toEpochMilli()
                ).toString(2))

                usagesList.add(Pair(date.toString(), usage))
                date = nextDate
            }

            var text = ""
            for ((d, usage) in usagesList) {
                text += "Usage data for $d:\n"

                for ((app, seconds) in usage) {
                    text += "$app ${humanizeTime(seconds)}\n"
                }
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
                findNavController().navigate(R.id.action_debrief_to_login)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}