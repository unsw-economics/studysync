package au.edu.unsw.business.studysync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import au.edu.unsw.business.studysync.constants.Constants.GROUP_CONTROL
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE
import au.edu.unsw.business.studysync.usage.UsageStatsNegotiator
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainViewModel

    val preferences by lazy {
        getSharedPreferences("studysync-config", Context.MODE_PRIVATE)
    }

    val database by lazy {
        (application as StudySyncApplication).database
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        vm = ViewModelProvider(
            this,
            MainViewModelFactory(preferences, database)
        ).get(MainViewModel::class.java)

        val isIdentified = vm.identified.value!!
        val isPermitted = UsageStatsNegotiator.hasUsageStatsPermission(applicationContext)
        val isBaseline = LocalDate.now().isBefore(TREATMENT_START_DATE)
        val isTreatment = vm.group.value!! > GROUP_CONTROL
        val isTreatmentDebriefed = vm.treatmentDebriefed.value!!

        if (isIdentified) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            val navController = navHostFragment.navController

            when {
                !isPermitted ->
                    navController.navigate(R.id.action_login_to_request_permission)
                isBaseline ->
                    navController.navigate(
                        R.id.action_login_to_terminal,
                        bundleOf(
                            Pair("title", "Baseline Title"),
                            Pair("body", "Baseline Body")
                        )
                    )
                !isTreatment ->
                    navController.navigate(
                        R.id.action_login_to_terminal,
                        bundleOf(
                            Pair("title", "Control Title"),
                            Pair("body", "Control Body")
                        )
                    )
                !isTreatmentDebriefed ->
                    navController.navigate(R.id.action_login_to_debrief)
                else ->
                    navController.navigate(R.id.action_login_to_treatment)
            }

        }
    }
}