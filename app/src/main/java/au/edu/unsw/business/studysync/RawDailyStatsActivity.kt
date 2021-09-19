package au.edu.unsw.business.studysync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import au.edu.unsw.business.studysync.MainActivity.Companion.RAW_STATS_TEXT

class RawDailyStatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raw_daily_stats)

        val statsTextView = findViewById<TextView>(R.id.stats_text)

        statsTextView.text = intent?.extras?.getString(RAW_STATS_TEXT).toString()
        statsTextView.movementMethod = ScrollingMovementMethod()
    }
}