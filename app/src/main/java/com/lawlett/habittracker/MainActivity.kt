package com.lawlett.habittracker

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.lawlett.habittracker.databinding.ActivityMainBinding
import com.lawlett.habittracker.ext.TAG
import com.lawlett.habittracker.ext.changeLanguage
import com.lawlett.habittracker.ext.checkedTheme
import com.lawlett.habittracker.ext.loadLocale
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.Key.IS_SETTING
import com.lawlett.habittracker.helper.MyFirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    //Ibrahim
    private val binding: ActivityMainBinding by viewBinding()
    private lateinit var navController: NavController

    @Inject
    lateinit var cacheManager: CacheManager
    var appUpdateManager: AppUpdateManager? = null
    private val UPDATE_CODE = 22

    override fun onCreate(savedInstanceState: Bundle?) {
        checkedTheme()
        changeLanguage()
        loadLocale(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initNavigationGraph()
        destinationListener()
        MyFirebaseMessagingService()
        askNotificationPermission()
    }

    fun isClickableBottom(isClickable: Boolean) {
        binding.bottomNavigation.menu.forEach { it.isEnabled = isClickable }
    }

    private fun popUp() {
        val snackbar = Snackbar.make(
            findViewById(androidx.appcompat.R.id.content),
            "App Update Almost Done.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(
            "Reload"
        ) { appUpdateManager?.completeUpdate() }
        snackbar.setTextColor(Color.parseColor("#FF0000"))
        snackbar.show()
    }

    val listener = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp()
        }
    }

    private fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        this@MainActivity,
                        UPDATE_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                    Log.e("ololo", "onSuccess:$e ")
                }
            }
        }?.addOnFailureListener {
            Log.e("ololo", "onFailure: $it")
        }
        appUpdateManager?.registerListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("ololo", "onActivityResult: RESULT_OK")
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.notification_not_come), Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun destinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = when (destination.id) {
                R.id.habitDetailFragment -> false
                else -> true
            }

            binding.toolbarMain.isVisible = when (destination.id) {
                R.id.habitDetailFragment -> false
                else -> true
            }
            when (destination.id) {
                R.id.badHabitFragment -> {
                    checkUpdate()
                }
            }

            binding.toolbarMain.text = when (destination.id) {
                R.id.badHabitFragment -> getString(R.string.bad_habit)
                R.id.goodHabitFragment -> getString(R.string.good_habit)
                R.id.followFragment -> getString(R.string.tv_subscriptions)
                R.id.settingsFragment -> getString(R.string.tv_settings)
                else -> getString(R.string.tv_main)
            }
        }
    }

    private fun initNavigationGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setupWithNavController(navController)
    }
}