package au.edu.unsw.business.studysync.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import au.edu.unsw.business.studysync.MainViewModel
import au.edu.unsw.business.studysync.MainViewModelFactory
import au.edu.unsw.business.studysync.R
import au.edu.unsw.business.studysync.databinding.FragmentBaselineIntroBinding
import au.edu.unsw.business.studysync.databinding.FragmentTerminalBinding

class TerminalFragment : Fragment() {

    private var _binding: FragmentTerminalBinding? = null
    private val binding get() = _binding!!

    val args: TerminalFragmentArgs by navArgs()

    val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTerminalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title = args.title
        binding.body = args.body

        binding.clearDataButton.setOnClickListener {
            vm.clearData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}