package com.example.testrpgroupwebview.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.testrpgroupwebview.R
import com.example.testrpgroupwebview.databinding.FragmentNoInternetBinding
import com.example.testrpgroupwebview.utils.ConnectionLiveData

class NoInternetFragment : Fragment() {

    lateinit var connectionLiveData: ConnectionLiveData

    private var _binding: FragmentNoInternetBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentNoInternetBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoInternetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCheckingInternetStatus()
        requireActivity().setTitle(R.string.no_internet_fragment_label)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initCheckingInternetStatus() {
        connectionLiveData = ConnectionLiveData(requireActivity().application)
        connectionLiveData.observe(viewLifecycleOwner) { isNetConnected ->
            if (isNetConnected) {
                findNavController().navigate(R.id.action_noInternetFragment_to_webViewFragment)
            }
        }
    }
}