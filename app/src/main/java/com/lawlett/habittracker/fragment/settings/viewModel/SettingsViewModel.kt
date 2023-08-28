package com.lawlett.habittracker.fragment.settings.viewModel

import androidx.lifecycle.ViewModel
import com.lawlett.habittracker.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: Repository):ViewModel() {

//    fun isUserSeen(): Boolean {
//        return repository.isUserSetting()
//    }
//
//    fun saveUserSeen() {
//        repository.saveSettings()
//    }
}