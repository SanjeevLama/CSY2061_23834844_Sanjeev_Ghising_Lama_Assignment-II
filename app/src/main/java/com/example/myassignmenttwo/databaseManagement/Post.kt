package com.example.myassignmenttwo.databaseManagement


data class Post(
    val postId: Int,
    val userId: Int,
    val caption: String?,
    val imageUri: String?,
    val postedDate: String
)