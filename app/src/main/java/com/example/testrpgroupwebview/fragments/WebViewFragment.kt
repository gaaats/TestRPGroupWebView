package com.example.testrpgroupwebview.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.testrpgroupwebview.MainVievModel
import com.example.testrpgroupwebview.R
import com.example.testrpgroupwebview.databinding.FragmentWebViewBinding
import com.example.testrpgroupwebview.utils.ConnectionLiveData
import com.example.testrpgroupwebview.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped

@AndroidEntryPoint
@ActivityScoped
class WebViewFragment : Fragment() {

    private val connectionLiveData by lazy {
        ConnectionLiveData(requireActivity().application)
    }

    private val mainVievModel by activityViewModels<MainVievModel>()

    private var _binding: FragmentWebViewBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentWebViewBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)

        mainVievModel.initFirstLoad.observe(viewLifecycleOwner){ itsFirstLoadOfFragment->
            //need to make init load, маленький костиль)
            if (itsFirstLoadOfFragment && !mainVievModel.oneTimeCheckInternet()){
                mainVievModel.makeLoadVebViev(binding.webViewInfo)
            }
        }

        connectionLiveData.observe(viewLifecycleOwner) { isNetConnected ->
            if (!isNetConnected) {
                findNavController().navigate(R.id.action_webViewFragment_to_noInternetFragment)
            } else {
                mainVievModel.makeLoadVebViev(binding.webViewInfo)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().setTitle(R.string.veb_viev_fragment_label)
        initBackNav()

        initObserveForLoading()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initObserveForLoading() {
        mainVievModel.stateVebViev.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    Log.d("MY_TAG", "Loading")
                    initScreenLoading()
                }
                is Resource.Success -> {
                    Log.d("MY_TAG", "Success")
                    initScreenLoadSuccess()
                }
                is Resource.Error -> {
                    Log.d("MY_TAG", "Error")
                    findNavController().navigate(R.id.action_webViewFragment_to_noInternetFragment)
                }
            }
        }
    }

    private fun initScreenLoadSuccess() {
        binding.webViewInfo.alpha = 1F
        binding.progBarOnVebViewInfo.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initScreenLoading() {
        binding.webViewInfo.alpha = 0.1F
        binding.progBarOnVebViewInfo.visibility = View.VISIBLE
    }

    private fun initBackNav() {
        binding.webViewInfo.setOnKeyListener { view, key, event ->
            if (event.action == KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_BACK) {
                if (binding.webViewInfo.canGoBack()) {
                    binding.webViewInfo.goBack()
                } else {
                    requireActivity().finish()
                }
            }
            true
        }
    }

    override fun onPause() {
        val page = binding.webViewInfo.url
        mainVievModel.saveCurrentPageLoaded(page)
        Log.d("MY_TAG", "save value in fragment is ${page}")
        super.onStop()
    }
}