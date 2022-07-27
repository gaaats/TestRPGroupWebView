package com.example.testrpgroupwebview.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.testrpgroupwebview.MainVievModel
import com.example.testrpgroupwebview.R
import com.example.testrpgroupwebview.databinding.FragmentStartFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class StartFragmentFragment : Fragment() {

    private val mainVievModel by activityViewModels<MainVievModel>()

    private var _binding: FragmentStartFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentStartFragmentBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartFragmentBinding.inflate(inflater, container, false)
        oneTimeCheckingForUserAcceptPolicyOrNot()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initBtnSetOnClickListener()
//        initObserveForUserAcceptPolicy()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initBtnSetOnClickListener() {
        binding.btnAccept.setOnClickListener {
            mainVievModel.userAcceptPolicy()
            findNavController().navigate(R.id.action_startFragmentFragment_to_webViewFragment)
        }
        binding.btnDeny.setOnClickListener {
            mainVievModel.userDenyPolicy()
        }
        binding.textAlreadyAcceptPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_startFragmentFragment_to_webViewFragment)
        }
    }

    private fun oneTimeCheckingForUserAcceptPolicyOrNot() {
        val isUserAcceptPolicy = mainVievModel.isUserAcceptPolicy.value ?: false
        if (isUserAcceptPolicy) {
            binding.scrollViev.visibility = View.GONE
            binding.tvTitle.visibility = View.GONE
            binding.textAlreadyAcceptPolicy.visibility = View.VISIBLE
            return
        }
//        binding.scrollViev.visibility = View.VISIBLE
//        binding.textAlreadyAcceptPolicy.visibility = View.GONE
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}