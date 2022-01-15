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
import au.edu.unsw.business.studysync.constants.Constants.GROUP_INTERCEPT
import au.edu.unsw.business.studysync.databinding.FragmentDebriefBinding
import au.edu.unsw.business.studysync.support.MessageUtils
import au.edu.unsw.business.studysync.support.TimeUtils
import au.edu.unsw.business.studysync.viewmodels.MainViewModel
import au.edu.unsw.business.studysync.viewmodels.TreatmentViewModel
import au.edu.unsw.business.studysync.viewmodels.TreatmentViewModelFactory

class DebriefFragment : Fragment() {

    private var _binding: FragmentDebriefBinding? = null
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
        _binding = FragmentDebriefBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val limit = TimeUtils.humanizeTimeHm(vm.subjectSettings.treatmentLimit.value!!)
        val highlightColor = ContextCompat.getColor(requireContext(), R.color.light_green)

        binding.body = if (vm.subjectSettings.testGroup.value!! == GROUP_INTERCEPT) {
            MessageUtils.treatmentInterceptDebrief(limit, highlightColor)
        } else {
            MessageUtils.treatmentAffineDebrief(treatmentVm.dailyIncentive.value!!, limit, highlightColor)
        }

        binding.continueButton.setOnClickListener {
            vm.completeDebrief()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}