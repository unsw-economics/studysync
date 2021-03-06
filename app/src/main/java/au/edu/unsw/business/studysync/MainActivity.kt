package au.edu.unsw.business.studysync
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import au.edu.unsw.business.studysync.constants.Constants.DAILY_SCHEDULER_WORK
import au.edu.unsw.business.studysync.constants.Constants.GROUP_AFFINE
import au.edu.unsw.business.studysync.constants.Constants.GROUP_CONTROL
import au.edu.unsw.business.studysync.constants.Constants.GROUP_INTERCEPT
import au.edu.unsw.business.studysync.constants.Constants.GROUP_UNASSIGNED
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_BASELINE
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_ENDLINE
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_EXPERIMENT
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_OVER
import au.edu.unsw.business.studysync.constants.Constants.PERODIC_SUBMIT_USAGE_WORK
import au.edu.unsw.business.studysync.constants.Constants.PERODIC_UPDATE_DATES_WORK
import au.edu.unsw.business.studysync.network.RobustFetchTestParameters
import au.edu.unsw.business.studysync.support.MessageUtils
import au.edu.unsw.business.studysync.support.TimeUtils
import au.edu.unsw.business.studysync.support.UsageUtils
import au.edu.unsw.business.studysync.viewmodels.MainViewModel
import au.edu.unsw.business.studysync.viewmodels.MainViewModelFactory
import au.edu.unsw.business.studysync.workers.DailySchedulerWorker
import au.edu.unsw.business.studysync.workers.UpdateStudyDatesWorker
import au.edu.unsw.business.studysync.workers.UsageWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {

    private lateinit var vm: MainViewModel
    private lateinit var navController: NavController

    private val application by lazy {
        getApplication() as StudySyncApplication
    }

    private val subjectSettings by lazy {
        application.subjectSettings
    }

    private val usageDriver by lazy {
        application.usageDriver
    }

    private val navOptions by lazy {
        NavOptions.Builder()
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setPopUpTo(R.id.nav_graph, true)
            .build()
    }

    private val workManager by lazy {
        WorkManager.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        supportActionBar?.hide()

        vm = ViewModelProvider(this, MainViewModelFactory(application)).get(
            MainViewModel::class.java)

        navigate()

        val period = TimeUtils.getTodayPeriod()

        if (period == PERIOD_OVER) return

        if (vm.usageAccessEnabled.value!!) {
            lifecycleScope.launch(Dispatchers.IO) {
                usageDriver.recordNewUsages()
            }
        }

        vm.usageAccessEnabled.observe(this) {
            if (it) {
                lifecycleScope.launch(Dispatchers.IO) {
                    usageDriver.recordNewUsages()
                }
            }
        }

        vm.navigateEvents.subscribe {
            navigate()
        }

        if (period == PERIOD_EXPERIMENT && subjectSettings.identified.value!! && subjectSettings.testGroup.value!! == GROUP_UNASSIGNED) {
            lifecycleScope.launch {
                val result = RobustFetchTestParameters.fetchOrScheduleRetry(applicationContext, subjectSettings.authToken.value!!, subjectSettings.subjectId.value!!)

                if (result.isSuccess) {
                    val data = result.getOrNull()!!
                    subjectSettings.setTestParameters(data.first, data.second, data.third)
                }
            }
        }

        workManager.enqueueUniqueWork(DAILY_SCHEDULER_WORK, ExistingWorkPolicy.REPLACE, DailySchedulerWorker.createRequestForNext0001())

        workManager.enqueueUniquePeriodicWork(PERODIC_SUBMIT_USAGE_WORK, ExistingPeriodicWorkPolicy.REPLACE, UsageWorker.createRequest())

        workManager.enqueueUniquePeriodicWork(PERODIC_UPDATE_DATES_WORK, ExistingPeriodicWorkPolicy.REPLACE, UpdateStudyDatesWorker.createPerodicRequest())

        Log.d("App/MainActivity", "DailySchedulerWorker enqueued")
    }

    override fun onResume() {
        super.onResume()

        val isPermitted = UsageUtils.hasUsageStatsPermission(applicationContext)
        val period = TimeUtils.getTodayPeriod()

        if (vm.usageAccessEnabled.value != isPermitted) {
            vm.setUsageAccessEnabled(isPermitted)

            if (navController.currentDestination!!.id != R.id.RequestPermissionFragment) {
                navigate()
            }
        } else if (
            (subjectSettings.testGroup.value!! == GROUP_INTERCEPT || subjectSettings.testGroup.value!! == GROUP_AFFINE)
            && navController.currentDestination!!.id == R.id.TerminalFragment
            && period == PERIOD_EXPERIMENT
        ) {
            navigate()
        } else if (period == PERIOD_OVER || period == PERIOD_ENDLINE) {
            navigate()
        }
    }

    fun navigate() {
        val period = TimeUtils.getTodayPeriod()
        val isIdentified = subjectSettings.identified.value!!
        val isPermitted = vm.usageAccessEnabled.value!!
        val isTreatment = subjectSettings.testGroup.value!! > GROUP_CONTROL
        val isTreatmentDebriefed = subjectSettings.treatmentDebriefed.value!!

        when {
            period == PERIOD_OVER ->
                navigateIfDifferent(
                    R.id.TerminalFragment,
                    bundleOf(
                        Pair("title", getString(R.string.over_title)),
                        Pair("body", getString(R.string.over_body))
                    )
                )
            period == PERIOD_ENDLINE ->
                navigateIfDifferent(
                    R.id.TerminalFragment,
                    bundleOf(
                        Pair("title", getString(R.string.endline_title)),
                        Pair("body", MessageUtils.endlineBody(ContextCompat.getColor(application, R.color.light_green)).toString())
                    )
                )
            !isIdentified ->
                navigateIfDifferent(R.id.LoginFragment)
            !isPermitted ->
                navigateIfDifferent(R.id.RequestPermissionFragment)
            period == PERIOD_BASELINE ->
                navigateIfDifferent(
                    R.id.TerminalFragment,
                    bundleOf(
                        Pair("title", getString(R.string.baseline_title)),
                        Pair("body", MessageUtils.baselineBody(ContextCompat.getColor(application, R.color.light_green)).toString())
                    )
                )
            !isTreatment ->
                navigateIfDifferent(
                    R.id.TerminalFragment,
                    bundleOf(
                        Pair("title", getString(R.string.control_title)),
                        Pair("body", getString(R.string.control_body))
                    )
                )
            !isTreatmentDebriefed ->
                navigateIfDifferent(R.id.DebriefFragment)
            else ->
                navigateIfDifferent(R.id.TreatmentFragment)
        }
    }

    private fun navigateIfDifferent(resId: Int) {
        navigateIfDifferent(resId, null)
    }

    private fun navigateIfDifferent(resId: Int, args: Bundle?) {
        val destination = navController.currentDestination!!

        // second part of this conditional is a hack for changing between TerminalFragments
        if (destination.id != resId || resId == R.id.TerminalFragment) {
            navController.navigate(resId, args, navOptions)
        }
    }
}