package com.example.myassignmenttwo.databaseManagement

data class User(
    val userId: Int,
    val fullName: String,
    val email: String,
    val dob: String,
    val dateCreated: String,
    val dateUpdated: String,
    val password: String
)