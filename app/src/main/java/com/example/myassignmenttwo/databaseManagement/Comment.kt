package com.example.myassignmenttwo.databaseManagement

data class Comment(
    val commentId: Long,
    val postId: Long,
    val userId: Long,
    val text: String
)

