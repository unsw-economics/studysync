package au.edu.unsw.business.studysync

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import au.edu.unsw.business.studysync.constants.Constants.DEBUG_DATA

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        val statsTextView = findViewById<TextView>(R.id.stats_text)

        statsTextView.text = intent?.extras?.getString(DEBUG_DATA).toString()
        statsTextView.movementMethod = ScrollingMovementMethod()
    }
}