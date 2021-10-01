package au.edu.unsw.business.studysync.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import au.edu.unsw.business.studysync.*
import au.edu.unsw.business.studysync.databinding.FragmentTreatmentBinding
import au.edu.unsw.business.studysync.logic.TimeUtils.midnight
import au.edu.unsw.business.studysync.logic.TimeUtils.now
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer.computeUsage

class TreatmentFragment: Fragment() {

    private var _binding: FragmentTreatmentBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()
    private val treatmentVm: TreatmentViewModel by viewModels {
        val activity = requireActivity() as MainActivity
        TreatmentViewModelFactory(activity.application as StudySyncApplication)
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

        binding.clearDataButton.setOnClickListener {
            vm.clearData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}