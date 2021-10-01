package au.edu.unsw.business.studysync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import au.edu.unsw.business.studysync.constants.Constants.GROUP_CONTROL
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE
import au.edu.unsw.business.studysync.usage.UsageStatsNegotiator
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainViewModel
    private lateinit var navController: NavController

    private val navOptions by lazy {
        NavOptions.Builder()
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setPopUpTo(R.id.nav_graph, true)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        supportActionBar?.hide()

        vm = ViewModelProvider(this, MainViewModelFactory(application as StudySyncApplication)).get(MainViewModel::class.java)

        vm.navigateEvents.subscribe {
            navigate()
        }

        navigate()
    }

    override fun onResume() {
        super.onResume()

        val isPermitted = UsageStatsNegotiator.hasUsageStatsPermission(applicationContext)

        if (vm.usageAccessEnabled.value != isPermitted) {
            vm.setUsageAccessEnabled(isPermitted)

            if (navController.currentDestination!!.id != R.id.RequestPermissionFragment) {
                navigate()
            }
        }
    }

    fun navigate() {
        val isIdentified = vm.subjectSettings.identified.value!!
        val isPermitted = vm.usageAccessEnabled.value!!
        val isBaseline = LocalDate.now().isBefore(TREATMENT_START_DATE)
        val isTreatment = vm.subjectSettings.treatmentGroup.value!! > GROUP_CONTROL
        val isTreatmentDebriefed = vm.subjectSettings.treatmentDebriefed.value!!

        when {
            !isIdentified ->
                navigateIfDifferent(R.id.LoginFragment)
            !isPermitted ->
                navigateIfDifferent(R.id.RequestPermissionFragment)
            isBaseline ->
                navigateIfDifferent(
                    R.id.TerminalFragment,
                    bundleOf(
                        Pair("title", "Baseline Title"),
                        Pair("body", "Baseline Body")
                    )
                )
            !isTreatment ->
                navigateIfDifferent(
                    R.id.TerminalFragment,
                    bundleOf(
                        Pair("title", "Control Title"),
                        Pair("body", "Control Body")
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
        if (navController.currentDestination!!.id != resId) {
            navController.navigate(resId, args, navOptions)
        }
    }
}