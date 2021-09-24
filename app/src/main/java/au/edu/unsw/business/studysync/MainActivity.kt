package au.edu.unsw.business.studysync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import java.util.*

const val RAW_STATS_TEXT = "au.edu.unsw.business.studysync.RAW_STATS_TEXT"

class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainViewModel

    private val preferences by lazy {
        getSharedPreferences("studysync-config", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm = ViewModelProvider(this, MainViewModelFactory(preferences)).get(MainViewModel::class.java)

        if (vm.identified.value!!) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            val navController = navHostFragment.navController

            navController.navigate(R.id.action_login_to_debrief)
        }
    }

}