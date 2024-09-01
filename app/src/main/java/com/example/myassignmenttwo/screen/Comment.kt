package com.example.myassignmenttwo.screen


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myassignmenttwo.R
import com.example.myassignmenttwo.databaseManagement.Comment
import com.example.myassignmenttwo.databaseManagement.DatabaseHelper
import com.example.myassignmenttwo.navigation.Routes
import com.example.myassignmenttwo.navigation.SharedViewModel

@Composable
fun Comment(
    postId: Int,
    navController: NavController,
    viewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val currentUserId = viewModel.userId.value
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    var commentInput by remember {
        mutableStateOf("")
    }
    val comments = remember {
        mutableStateOf(listOf<Comment>())
    }
    // Get the current keyboard height
    val imeInsets = WindowInsets.ime
    val imeHeight = with(LocalDensity.current) { imeInsets.getBottom(LocalDensity.current).toDp() }

    // Calculate yOffset based on keyboard visibility
    val yOffset by animateDpAsState(
        targetValue = if (imeHeight > 0.dp) (-280).dp else 0.dp, label = "comment field animation"
    )

    // Load comments when the composable is first launched
    LaunchedEffect(key1 = Unit) {
        comments.value = dbHelper.getAllCommentsForPost(postId.toLong())
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = { navController.navigate(Routes.home) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back icon"
                    )
                }

                Spacer(modifier = Modifier.width(80.dp))

                Text(
                    text = "Comment",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn {
                itemsIndexed(comments.value){index, comment ->
                    CommentUi(comment,dbHelper)
                }
            }

        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.BottomCenter)
                .offset(y = yOffset)


        ) {
            TextField(
                value = commentInput,
                onValueChange = { commentInput = it },
                placeholder = { Text(text = "Add a comment") },
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            IconButton(
                onClick = {
                    if (currentUserId != null && commentInput.isNotBlank()) {
                        // Add comment to the database
                        dbHelper.addComment(postId.toLong(), currentUserId, commentInput)

                        // Clear the input field
                        commentInput = ""

                        // Reload comments after adding a new comment
                        comments.value = dbHelper.getAllCommentsForPost(postId.toLong())
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "send icon"
                )
            }
        }
    }
}

@Composable
fun CommentUi(
    comment: Comment,
    dbHelper: DatabaseHelper
) {
    val userName = dbHelper.getUserNameById(comment.userId.toInt())

    Row(
        modifier = Modifier.padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "profile",
            Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(text = userName, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
            Text(
                text = comment.text,
                modifier = Modifier.padding(top = 5.dp),
                fontSize = 16.sp
            )
        }
    }
}

