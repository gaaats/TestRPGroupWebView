package com.example.testrpgroupwebview.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.testrpgroupwebview.MainVievModel
import com.example.testrpgroupwebview.R
import com.example.testrpgroupwebview.databinding.FragmentWebViewBinding
import com.example.testrpgroupwebview.utils.ConnectionLiveData
import com.example.testrpgroupwebview.utils.Resource

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
        mainVievModel.makeLoadVebViev(binding.webViewInfo)

        connectionLiveData.observe(viewLifecycleOwner) { isNetConnected ->
            if (!oneTimeCheckInternet()) {
                Log.d("MY_TAG", "Net Noooooooo")
                findNavController().navigate(R.id.action_webViewFragment_to_noInternetFragment)
            } else {
                Log.d("MY_TAG", "Net Connected ")
                mainVievModel.makeLoadVebViev(binding.webViewInfo)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

//    private fun tryMakeLoad(pathForLoad: String = "https://geekshopukraine.com.ua/ua/") {
//        lifecycleScope.launch {
//            binding.webViewInfo.apply {
//                try {
//                    webViewClient = WebViewClient()
//                    loadUrl(mainVievModel.currentPageOpened.value?: pathForLoad)
//                    delay(2000)
//                    binding.webViewInfo.alpha = 1F
//                    binding.progBarOnVebViewInfo.visibility = View.INVISIBLE
//                } catch (e: Throwable) {
//                    findNavController().navigate(R.id.action_webViewFragment_to_noInternetFragment)
//                }
//            }
//        }
//    }

    private fun initScreenLoading() {
//        initCheckingInternetStatus()
        binding.webViewInfo.alpha = 0.1F
        binding.progBarOnVebViewInfo.visibility = View.VISIBLE
    }

    private fun initBackNav() {
        binding.webViewInfo.setOnKeyListener { view, key, event ->
            if ((event.action == KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_BACK) && binding.webViewInfo.canGoBack()) {
                binding.webViewInfo.goBack()
            }
            if (!binding.webViewInfo.canGoBack()) {
                findNavController().navigate(R.id.action_webViewFragment_to_startFragmentFragment)
            }
            true
        }
    }

    private fun initCheckingInternetStatus() {
        Log.d("MY_TAG", "initCheckingInternetStatus  WebViewFragment")

        connectionLiveData.observe(viewLifecycleOwner) { isNetConnected ->
            if (isNetConnected) {
                Log.d("MY_TAG", "Net Connected ")
                mainVievModel.makeLoadVebViev(binding.webViewInfo)
            }
            if (!isNetConnected) {
                Log.d("MY_TAG", "Net Noooooooo")
                findNavController().navigate(R.id.action_webViewFragment_to_noInternetFragment)
            }
        }
    }

    override fun onStop() {
        val page = binding.webViewInfo.url
        mainVievModel.saveCurrentPageLoaded(page)
        Log.d("MY_TAG", "current page is: $page")
        super.onStop()
    }

    fun oneTimeCheckInternet(): Boolean {
        val connectivityManager =
            requireActivity().application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return if (networkInfo != null && networkInfo.isConnected) {
            Log.d("MY_TAG", "Net +++ in oneTimeCheckInternet")
            true
        } else {
            Log.d("MY_TAG", "Net --- in oneTimeCheckInternet")
            false
        }
    }
}