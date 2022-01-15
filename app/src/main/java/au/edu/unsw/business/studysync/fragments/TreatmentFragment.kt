package au.edu.unsw.business.studysync.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import au.edu.unsw.business.studysync.MainActivity
import au.edu.unsw.business.studysync.R
import au.edu.unsw.business.studysync.StudySyncApplication
import au.edu.unsw.business.studysync.databinding.FragmentTreatmentBinding
import au.edu.unsw.business.studysync.support.MessageUtils
import au.edu.unsw.business.studysync.support.TimeUtils
import au.edu.unsw.business.studysync.viewmodels.MainViewModel
import au.edu.unsw.business.studysync.viewmodels.TreatmentViewModel
import au.edu.unsw.business.studysync.viewmodels.TreatmentViewModelFactory

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
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = vm
        binding.treatmentVm = treatmentVm
        binding.timeUtils = TimeUtils
        binding.messageUtils = MessageUtils

        treatmentVm.todayUsage.observe(viewLifecycleOwner) {
            if (!TimeUtils.lessThan(it, vm.subjectSettings.treatmentLimit.value!!)) {
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        treatmentVm.setTodayUsage()
        treatmentVm.setSuccesses()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}