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
import au.edu.unsw.business.studysync.StudySyncApplication
import au.edu.unsw.business.studysync.viewmodels.MainViewModel
import au.edu.unsw.business.studysync.constants.Constants.DEBUG_DATA
import au.edu.unsw.business.studysync.database.DbAppReport
import au.edu.unsw.business.studysync.database.DbReport
import au.edu.unsw.business.studysync.databinding.FragmentBaselineIntroBinding
import au.edu.unsw.business.studysync.development.DevUtils.printList
import au.edu.unsw.business.studysync.support.TimeUtils.getStudyPeriodAndDay
import au.edu.unsw.business.studysync.support.TimeUtils.humanizeTimeHms
import au.edu.unsw.business.studysync.support.TimeUtils.midnight
import au.edu.unsw.business.studysync.support.TimeUtils.now
import au.edu.unsw.business.studysync.support.TimeUtils.toMilliseconds
import au.edu.unsw.business.studysync.network.ReportPayload
import au.edu.unsw.business.studysync.network.ServerAppReport
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.usage.UsageDriver
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

        /*
        binding.clearDataButton.setOnClickListener {
            lifecycleScope.launch {
                vm.clearData()
            }
        }

        val usageDriver = (requireActivity().application as StudySyncApplication).usageDriver

        binding.submitReportButton.setOnClickListener {
            MainScope().launch {
                usageDriver.submitUnsyncedReports()
            }
        }

        binding.recordNewUsagesButton.setOnClickListener {
            MainScope().launch {
                usageDriver.recordNewUsages()
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
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}