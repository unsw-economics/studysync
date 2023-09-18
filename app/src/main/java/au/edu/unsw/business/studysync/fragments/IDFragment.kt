package au.edu.unsw.business.studysync.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import au.edu.unsw.business.studysync.R

class IDFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var idTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overlay_id, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idTextView = view.findViewById(R.id.idTextView)
        sharedPref = requireActivity().getSharedPreferences("studysync-config", Context.MODE_PRIVATE)

        // Immediately update the TextView with the current value
        val subjectId = sharedPref.getString("subject-id", "") ?: ""
        idTextView.text = if (subjectId.isNotEmpty()) "Participant ID: $subjectId" else ""
    }

    override fun onResume() {
        super.onResume()
        // Register the listener when the fragment is visible
        sharedPref.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the listener to avoid memory leaks
        sharedPref.unregisterOnSharedPreferenceChangeListener(this)
    }

    // This method is called when any shared preference changes
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "subject-id") {
            val subjectId = sharedPref.getString("subject-id", "") ?: ""
            idTextView.text = if (subjectId.isNotEmpty()) "Participant ID: $subjectId" else ""
        }
    }
}
