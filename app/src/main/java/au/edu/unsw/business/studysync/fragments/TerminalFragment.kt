package au.edu.unsw.business.studysync.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import au.edu.unsw.business.studysync.databinding.FragmentTerminalBinding
import au.edu.unsw.business.studysync.viewmodels.MainViewModel

class TerminalFragment: Fragment() {

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}