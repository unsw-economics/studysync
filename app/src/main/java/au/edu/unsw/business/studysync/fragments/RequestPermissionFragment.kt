package au.edu.unsw.business.studysync.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import au.edu.unsw.business.studysync.MainActivity
import au.edu.unsw.business.studysync.MainViewModel
import au.edu.unsw.business.studysync.R
import au.edu.unsw.business.studysync.databinding.FragmentLoginBinding
import au.edu.unsw.business.studysync.databinding.FragmentRequestPermissionBinding
import au.edu.unsw.business.studysync.usage.UsageStatsNegotiator

class RequestPermissionFragment : Fragment() {

    private var _binding: FragmentRequestPermissionBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestPermissionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = vm

        binding.requestPermissionButton.setOnClickListener {
            UsageStatsNegotiator.openUsageAccessPermissionsMenu(requireContext())
        }

        binding.continueButton.setOnClickListener {
            (activity as MainActivity).navigate()
        }
    }
}