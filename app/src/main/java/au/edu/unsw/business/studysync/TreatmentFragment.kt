package au.edu.unsw.business.studysync

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import au.edu.unsw.business.studysync.constants.Environment
import au.edu.unsw.business.studysync.databinding.FragmentLoginBinding
import au.edu.unsw.business.studysync.network.SyncApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TreatmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TreatmentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()
    private val treatmentVm: TreatmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treatment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        binding.requestPermissionButton.setOnClickListener {
            UsageStatsNegotiator.openUsageAccessPermissionsMenu(requireContext())
        }

        binding.requestStatsButton.setOnClickListener {
            val intent = Intent(activity, RawDailyStatsActivity::class.java).apply {
                putExtra(RAW_STATS_TEXT, UsageStatsNegotiator.getTodayUsageJson(requireContext()).toString(2))
            }

            startActivity(intent)
        }
        */
        // Set time spent today
        val now = ZonedDateTime.now()
        val midnight = now.toLocalDate().atStartOfDay(Environment.ZONE_ID)

        val usageMap = UsageStatsNegotiator.computeUsagesSerial(
            requireContext(),
            midnight.toInstant().toEpochMilli(),
            now.toInstant().toEpochMilli()
        )
        treatmentVm.setTimeSpentToday(usageMap.map{ it.value }.sum())


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TreatmentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TreatmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}