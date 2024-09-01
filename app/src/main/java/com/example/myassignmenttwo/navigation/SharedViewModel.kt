package com.example.myassignmenttwo.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _userId = mutableStateOf<Long?>(null)
    val userId: State<Long?> = _userId

    // Allow setting null to indicate logout
    fun setUserId(id: Long?) {
        _userId.value = id
    }

    // Function to log out the user
    fun logoutUser() {
        setUserId(null)
    }
}