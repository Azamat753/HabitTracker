package com.lawlett.habittracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.databinding.ActivityMainBinding
import com.lawlett.habittracker.ext.changeLanguage
import com.lawlett.habittracker.ext.checkedTheme
import com.lawlett.habittracker.ext.loadLocale
import com.lawlett.habittracker.helper.Key.IS_SETTING
import com.lawlett.habittracker.helper.MyFirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding: ActivityMainBinding by viewBinding()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        checkedTheme()
        changeLanguage()
        loadLocale(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initNavigationGraph()
        destinationListener()
        askNotificationPermission()
        MyFirebaseMessagingService()
        navigate()
    }

    private fun navigate() {
        if (intent.getBooleanExtra(IS_SETTING, false)) {
            navController.navigate(R.id.settingsFragment)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Уведомления не будут приходить", Toast.LENGTH_SHORT).show()
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

            binding.toolbarMain.text = when (destination.id) {
                R.id.mainFragment -> getString(R.string.tv_main)
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