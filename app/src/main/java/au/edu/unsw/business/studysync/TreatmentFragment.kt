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
import au.edu.unsw.business.studysync.databinding.FragmentTreatmentBinding
import au.edu.unsw.business.studysync.logic.TimeUtils.midnight
import au.edu.unsw.business.studysync.logic.TimeUtils.now
import au.edu.unsw.business.studysync.network.SyncApi
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer.computeUsage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class TreatmentFragment: Fragment() {

    private var _binding: FragmentTreatmentBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()
    private val treatmentVm: TreatmentViewModel by viewModels {
        val activity = requireActivity() as MainActivity
        TreatmentViewModelFactory(activity.preferences, activity.database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTreatmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val now = now()
        val midnight = midnight()

        val usageMap = computeUsage(requireContext(), now, midnight)

        treatmentVm.setTimeSpentToday(usageMap.map { it.value }.sum())
    }
}