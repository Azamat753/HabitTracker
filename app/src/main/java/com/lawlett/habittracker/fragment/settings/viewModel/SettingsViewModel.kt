package com.lawlett.habittracker.fragment.settings.viewModel

import androidx.lifecycle.ViewModel
import com.lawlett.habittracker.Repository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val repository: Repository):ViewModel() {

    fun isUserSeen(): Boolean {
        return repository.isUserSeen()
    }

    fun saveUserSeen() {
        repository.saveUserSeen()
    }
}