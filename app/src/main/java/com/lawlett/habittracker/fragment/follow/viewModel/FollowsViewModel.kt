package com.lawlett.habittracker.fragment.follow.viewModel

import androidx.lifecycle.ViewModel
import com.lawlett.habittracker.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowsViewModel @Inject constructor(private val repository: Repository):ViewModel() {

//    fun isUserSeen(): Boolean {
//        return repository.isUserFollow()
//    }
//
//    fun saveUserSeen() {
//        repository.saveUserFollow()
//    }
}