package com.lawlett.habittracker

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View.GONE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.databinding.ActivityMainBinding
import com.lawlett.habittracker.ext.loadLocale
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.lawlett.habittracker.helper.MyFirebaseMessagingService


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by viewBinding()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initNavigationGraph()
        destinationListener()
        askNotificationPermission()
        MyFirebaseMessagingService()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
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
//            if (destination.id == R.id.habitDetailFragment) {
//                binding.toolbarMain.visibility = GONE
//            }
//            binding.toolbarMain.text = when (destination.id) {
//                R.id.mainFragment -> getString(R.string.tv_main)
//                R.id.followFragment -> getString(R.string.tv_subscriptions)
//                R.id.settingsFragment -> getString(R.string.tv_settings)
//                //  R.id.habitDetailFragment -> "Детали"
//                else -> getString(R.string.tv_main)
//            }
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