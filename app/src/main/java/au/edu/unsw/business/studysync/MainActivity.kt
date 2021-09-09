package au.edu.unsw.business.studysync

import android.app.usage.UsageStatsManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.AppOpsManager
import android.content.Context
import android.os.Process


class MainActivity : AppCompatActivity() {
    private lateinit var requestButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestButton = findViewById<Button>(R.id.request_permission_button)
    }

    override fun onResume() {
        super.onResume()

        if (checkUsageStatsPermission()) {
            requestButton.text = "Permission Granted"
        } else {
            requestButton.text = "Request Permission"
            requestButton.setOnClickListener {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
    }

    private fun checkUsageStatsPermission(): Boolean {
        val appOpsManager = applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow("android:get_usage_stats", Process.myUid(), application.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
}