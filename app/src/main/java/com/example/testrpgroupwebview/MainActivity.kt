package com.example.testrpgroupwebview

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.testrpgroupwebview.databinding.ActivityMainBinding
import com.example.testrpgroupwebview.utils.ConnectionLiveData
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class MainActivity : AppCompatActivity() {

    private val ONESIGNAL_APP_ID = "deca7d93-f878-49fd-b211-0bc1a573c599"

    private val mainVievModel by viewModels<MainVievModel>()

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("ActivityMainBinding = null")
    private val sheredPref by lazy {
        getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)
    }

    private val connectionLiveData by lazy {
        ConnectionLiveData(application)
    }

    private val navController by lazy {
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navHostFrag.navController
    }
    private val isUserAcceptPolicy by lazy {
        sheredPref.getBoolean(KEY_TO_PREF_USER_ACCEPT_POLICY, false)
    }

    private val isFirstStart by lazy {
        sheredPref.getBoolean(KEY_TO_PREF_FIRST_START, true)
    }
    private var startDestination = R.id.webViewFragment

    private val navGraph by lazy {
        navController.navInflater.inflate(R.navigation.my_nav)
    }

    private var isInternetAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            this.setKeepOnScreenCondition {
//                oneTimeCheckInternet()
//                initCheckingInternetStatus()
                mainVievModel.isSplashScreenActive.value
            }
        }

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        if (isUserAcceptPolicy) {
            Log.d("MY_TAG", "user accept policy")
        } else {
            Log.d("MY_TAG", "user deny policy")
        }

        chechIsFirstStartAndIsUserAcceptPolicy()
        addStartDestinationOfNavHostFrag()
        observeFinishActivity()
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onStop() {
        Log.d("MY_TAG", "onStop")
        saveUserAcceptPolicyValue()
        super.onStop()
    }

    private fun observeFinishActivity() {
        mainVievModel.finishApp.observe(this) {
            finish()
        }
    }

    private fun addStartDestinationOfNavHostFrag() {
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }

    private fun chechIsFirstStartAndIsUserAcceptPolicy() {
        if (isFirstStart || !isUserAcceptPolicy) {
            startDestination = R.id.startFragmentFragment
            Log.d("MY_TAG", "first start")
            sheredPref.edit()
                .putBoolean(KEY_TO_PREF_FIRST_START, false)
                .apply()

        } else {
            Log.d("MY_TAG", "second start")
            startDestination = R.id.webViewFragment

        }
        Log.d("MY_TAG", "startDestination is $startDestination")
    }

    private fun saveUserAcceptPolicyValue() {
        mainVievModel.isUserAcceptPolicy.value?.let {
            if (!isUserAcceptPolicy) {
                Log.d("MY_TAG", "activity saved it ${mainVievModel.isUserAcceptPolicy.value}")
                sheredPref.edit()
                    .putBoolean(KEY_TO_PREF_USER_ACCEPT_POLICY, it)
                    .apply()
            }
        }
    }

    companion object {
        private const val APP_PREF = "APP_PREF"
        private const val KEY_TO_PREF_FIRST_START = "222"
        private const val KEY_TO_PREF_USER_ACCEPT_POLICY = "333"
        private const val KEY_FOR_ = "333"
    }
}