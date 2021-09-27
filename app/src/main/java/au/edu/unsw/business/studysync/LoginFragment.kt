package au.edu.unsw.business.studysync

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import au.edu.unsw.business.studysync.databinding.FragmentLoginBinding
import au.edu.unsw.business.studysync.network.SyncApi
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
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

        binding.identifyButton.setOnClickListener {
            val subjectId = binding.subjectIdField.text.toString()

            lifecycleScope.launch {
                try {
                    val response = SyncApi.service.identify(subjectId)

                    if (response.message != null) throw Exception(response.message)

                    val data = response.data!!
                    vm.identify(subjectId, data.authToken)
                } catch (e: Exception) {
                    Log.d("MainActivity", "Error: ${e.message}")
                }
            }
        }

        vm.identified.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_login_to_debrief)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        /*
        if (UsageStatsNegotiator.hasUsageStatsPermission(requireContext())) {
            binding.requestPermissionButton.text = "Permission Granted"
            binding.requestPermissionButton.isEnabled = false

            binding.requestStatsButton.isEnabled = true
        } else {
            binding.requestPermissionButton.text = "Request Permission"
            binding.requestPermissionButton.isEnabled = true

            binding.requestStatsButton.isEnabled = false
        }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}