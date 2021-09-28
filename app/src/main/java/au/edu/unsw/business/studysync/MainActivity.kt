package au.edu.unsw.business.studysync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import au.edu.unsw.business.studysync.constants.Constants.GROUP_CONTROL
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainViewModel

    val preferences by lazy {
        getSharedPreferences("studysync-config", Context.MODE_PRIVATE)
    }

    val dailyReportDao by lazy {
        (application as StudySyncApplication).database.dailyReportDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm = ViewModelProvider(
            this,
            MainViewModelFactory(preferences, dailyReportDao)
        ).get(MainViewModel::class.java)

        val isBaselinePeriod = LocalDate.now().isBefore(TREATMENT_START_DATE)

        if (vm.identified.value!! || !isBaselinePeriod) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            val navController = navHostFragment.navController

            navController.navigate(
                when {
                    isBaselinePeriod ->
                        R.id.action_login_to_baseline_intro
                    vm.group.value!! > GROUP_CONTROL ->
                        R.id.action_login_to_treatment
                    else ->
                        R.id.action_login_to_treatment
                }
            )
        }
    }

}