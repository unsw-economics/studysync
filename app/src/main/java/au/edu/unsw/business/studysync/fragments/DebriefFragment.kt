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
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_END_DATE
import au.edu.unsw.business.studysync.databinding.FragmentDebriefBinding
import au.edu.unsw.business.studysync.support.TimeUtils

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

        val lightGreen = ContextCompat.getColor(requireActivity().applicationContext, R.color.light_green)

        val bodySpan = SpannableStringBuilder(getString(R.string.debrief_body_1))
            .bold {
                color(lightGreen) {
                    append(String.format("$%.2f", treatmentVm.dailyIncentive.value!!))
                }
            }
            .append(getString(R.string.debrief_body_2))
            .bold {
                color(lightGreen) {
                    append(TimeUtils.humanizeTimeHm(vm.subjectSettings.treatmentLimit.value!!))
                }
            }
            .append(getString(R.string.debrief_body_3))
            .bold {
                color(lightGreen) {
                    append(TREATMENT_END_DATE.toString())
                }
            }
            .append(getString(R.string.debrief_body_4))

        binding.body = bodySpan.toSpannable()

        binding.continueButton.setOnClickListener {
            vm.completeDebrief()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}