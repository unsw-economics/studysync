package au.edu.unsw.business.studysync.fragments

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import au.edu.unsw.business.studysync.*
import au.edu.unsw.business.studysync.constants.Constants.GROUP_INTERCEPT
import au.edu.unsw.business.studysync.constants.Environment.OVER_DATE
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

        binding.body = if (vm.subjectSettings.testGroup.value!! == GROUP_INTERCEPT) {
            "Treatment 1 Debrief Body"
        } else {
            MessageUtils.treatmentAffineDebrief(
                treatmentVm.dailyIncentive.value!!,
                TimeUtils.humanizeTimeHm(vm.subjectSettings.treatmentLimit.value!!),
                ContextCompat.getColor(requireContext(), R.color.light_green)
            )
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