package com.example.myassignmenttwo.screen


import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myassignmenttwo.R
import com.example.myassignmenttwo.databaseManagement.DatabaseHelper
import com.example.myassignmenttwo.databaseManagement.Post
import com.example.myassignmenttwo.navigation.Routes
import com.example.myassignmenttwo.navigation.SharedViewModel
import com.example.myassignmenttwo.permission.RequestPermission
import com.example.myassignmenttwo.permission.getMediaStoreUri

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(viewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), navController: NavController) {
    val currentUserId = viewModel.userId.value

    val context = LocalContext.current // Obtain the context using LocalContext
    val dbHelper = DatabaseHelper(context)

    val posts = remember {
        mutableStateOf(listOf<Post>())
    }

    LaunchedEffect(key1 = Unit) {
        posts.value = dbHelper.getAllPosts()
    }

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
        ){
            Text(text = "Stu-gram", style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
                modifier = Modifier
                    .align(Alignment.Center)
            )

            IconButton(onClick = {
                navController.navigate(Routes.aboutUser)
            },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "profile"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


        LazyColumn {
            itemsIndexed(posts.value) { index, post ->
                val isLiked = remember { mutableStateOf(dbHelper.isPostLikedByUser(post.postId, post.userId)) }

                if (currentUserId != null) {
                    PostUi(post,dbHelper, isLiked.value, navController) {
                        val newState = !isLiked.value
                        if (newState) {
                            dbHelper.likePost(post.postId, currentUserId.toInt())
                        } else {
                            dbHelper.unlikePost(post.postId, currentUserId.toInt())
                        }
                        isLiked.value = newState
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostUi(post: Post, dbHelper: DatabaseHelper,isLiked: Boolean, navController: NavController ,onLikeClick: () -> Unit) {

    //retrieve  user name from user id
    val userName = dbHelper.getUserNameById(post.userId)
    //retrieve date difference
    val dateDiff = dbHelper.getTimeAgo(post.postedDate)
    //like count of post
    val likeCount = dbHelper.getLikeCountForPost(post.postId)
    //comment count of post
    val commentCount = dbHelper.getCommentCountForPost(post.postId)

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x682B2B2B), // Center color
                    Color(0xFFB3B0AE), // Outer color
                    Color(0xC4D1D1D0)  // Adding a lighter color for more depth
                ),
                center = Offset(0.7f, 0.5f), // Center point in the middle
                radius = 1000f // Radius of the gradient
            ),
            shape = RoundedCornerShape(10.dp)
        )
    ){
        Row (modifier = Modifier
            .padding(10.dp)
            .background(
                color = Color.White.copy(alpha = 0.4f),
                shape = RoundedCornerShape(
                    topStart = 25.dp,
                    topEnd = 10.dp,
                    bottomStart = 25.dp,
                    bottomEnd = 10.dp
                )
            ),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "profile",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(25.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = userName, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black))
                Text(text = dateDiff, fontSize = 12.sp, color = Color.Black.copy(alpha = 0.6f))
            }
        }

        post.caption?.let { caption ->
            Text(
                text = caption,
                modifier = Modifier.padding(10.dp)
            )
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
        ){

            if (post.imageUri != null) {
                val context = LocalContext.current
                val imageUri = remember(post.imageUri) {
                    getMediaStoreUri(context, post.imageUri)
                }

                Log.d("ImageLoading", "Attempting to load image with URI: $imageUri")

                RequestPermission(
                    permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    },
                    rationale = "The app needs access to your images to display them.",
                    permissionNotAvailableContent = {
                        Text("Image permission denied")
                    }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Post Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        onLoading = { Log.d("ImageLoading", "Loading image: $imageUri") },
                        onSuccess = { Log.d("ImageLoading", "Successfully loaded image: $imageUri") },
                        onError = {
                            Log.e("ImageLoading", "Error loading image: $imageUri", it.result.throwable)
                            // Attempt to diagnose the issue
                            try {
                                context.contentResolver.openInputStream(imageUri)?.use {
                                    Log.d("ImageLoading", "Successfully opened input stream for $imageUri")
                                }
                            } catch (e: Exception) {
                                Log.e("ImageLoading", "Error opening input stream for $imageUri", e)
                            }
                        }
                    )
                }
            }
            Row(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
            ){
                Row(modifier = Modifier
                    .width(85.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically){

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "like",
                        tint = if (isLiked){
                            Color.Red
                        }
                        else{
                            Color.White
                        },
                        modifier = Modifier
                            .clickable {
                                onLikeClick()
                            }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(text = "$likeCount", color = Color.White)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Row(modifier = Modifier
                    .width(85.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Image(
                        painter = painterResource(id = R.drawable.baseline_comment_24),
                        contentDescription = "like",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Routes.comment+"/${post.postId}")
                            }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(text = "$commentCount", color = Color.White)
                }
            }

        }
    }
}
