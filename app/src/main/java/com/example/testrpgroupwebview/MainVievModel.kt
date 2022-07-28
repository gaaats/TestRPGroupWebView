package com.example.testrpgroupwebview

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testrpgroupwebview.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainVievModel @Inject constructor(private val application: Application) : ViewModel() {

    private val _isSplashScreenActive = MutableStateFlow(true)
    val isSplashScreenActive = _isSplashScreenActive.asStateFlow()

    private var _isUserAcceptPolicy = MutableLiveData<Boolean>()
    val isUserAcceptPolicy: LiveData<Boolean>
        get() = _isUserAcceptPolicy

    private var _stateVebViev = MutableLiveData<Resource<String>>()
    val stateVebViev: LiveData<Resource<String>>
        get() = _stateVebViev

    private var _finishApp = MutableLiveData<Unit>()
    val finishApp: LiveData<Unit>
        get() = _finishApp

    private var _currentPageOpened = MutableLiveData<String?>()
    val currentPageOpened: LiveData<String?>
        get() = _currentPageOpened

    private var _initFirstLoad = MutableLiveData(true)
    val initFirstLoad: LiveData<Boolean>
        get() = _initFirstLoad

    init {
        viewModelScope.launch {
            delay(2000)
            _isSplashScreenActive.value = false
        }
    }

    fun saveCurrentPageLoaded(path: String?) {
        _currentPageOpened.value = path
    }

    fun userAcceptPolicy() {
        _isUserAcceptPolicy.value = true
    }

    fun userDenyPolicy() {
        _isUserAcceptPolicy.value = false
        _finishApp.value = Unit
    }

    fun makeLoadVebViev(view: WebView) {
        _stateVebViev.value = Resource.Loading()
        view.webViewClient = object :WebViewClient(){
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                _stateVebViev.value = Resource.Error("no internet")
                super.onReceivedError(view, errorCode, description, failingUrl)
            }
        }
        try {
            viewModelScope.launch {
                view.loadUrl(currentPageOpened.value ?: FIRST_LOAD_PATH)
                delay(3000)
                _stateVebViev.value = Resource.Success("good load")
            }
        } catch (e: Exception) {
            Log.d("MY_TAG","Error is ${e.message}")
        }
        _initFirstLoad.value = false
    }

    fun oneTimeCheckInternet(): Boolean {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return if (networkInfo != null && networkInfo.isConnected) {
            Log.d("MY_TAG", "Net +++ in oneTimeCheckInternet")
            true
        } else {
            Log.d("MY_TAG", "Net --- in oneTimeCheckInternet")
            false
        }
    }

    companion object{
        private const val FIRST_LOAD_PATH = "https://geekshopukraine.com.ua/ua/"
    }
}