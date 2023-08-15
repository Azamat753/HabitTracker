package com.lawlett.habittracker

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by viewBinding()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initNavigationGraph()
        destinationListener()
    }

    private fun destinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = when (destination.id) {
                R.id.habitDetailFragment -> false
                else -> true
            }
            if (destination.id == R.id.habitDetailFragment) {
                binding.toolbarMain.visibility = GONE
            }
            binding.toolbarMain.title = when (destination.id) {
                R.id.mainFragment -> "Главная"
                R.id.friendsFragment -> "Подписки"
                R.id.settingsFragment -> "Настройки"
                //  R.id.habitDetailFragment -> "Детали"
                else -> "Главная"
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