package com.example.myassignmenttwo.screen

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun AboutUser(viewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), navController: NavController) {
    val currentUserId = viewModel.userId.value
    val context = LocalContext.current // Obtain the context using LocalContext
    val dbHelper = DatabaseHelper(context)
    val userPosts = remember {
        mutableStateOf(listOf<Post>())
    }
    LaunchedEffect(key1 = Unit) {
        if (currentUserId != null) {
            userPosts.value = dbHelper.getPostsByUserId(currentUserId.toInt())
        }
    }
    //retrieve  user name from user id
    val userName = currentUserId?.let { dbHelper.getUserNameById(it.toInt()) }
    val userPostCount = currentUserId?.let {dbHelper.getPostCountByUserId(it.toInt())}

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ){
            IconButton(onClick = {
                navController.popBackStack()
            },
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back icon")
            }
            Text(text = "Stu-gram", style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (userName != null && userPostCount != null) {
            UserInfoUi(userName, userPostCount, navController, currentUserId.toInt())
        }
        Spacer(modifier = Modifier.height(10.dp))
        UserPostTitle()
        Spacer(modifier = Modifier.height(5.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(userPosts.value){index, post ->
                UserAllPostUi(post, navController)
        }
    }

}
}

@Composable
fun UserInfoUi(username: String, userPostCount: Int, navController: NavController, currentUser: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "profile pic",
                modifier = Modifier
                    .size(100.dp)
                    .clip(
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = username,
                fontSize = 18.sp
            )
        }
        //Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "$userPostCount Posts",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            Button(onClick = {
                navController.navigate(Routes.editStudent+"/${currentUser}")
            },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF48aaff)
                )
                ) {
                Text(text = "Edit profile")
            }
        }
    }
}

@Composable
fun UserPostTitle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .shadow(
                elevation = 1.dp,
                shape = RectangleShape,
                ambientColor = DefaultShadowColor
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Posts",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(80.dp)
        )
    }
}

@Composable
fun UserAllPostUi(post: Post, navController: NavController) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(200.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x682B2B2B), // Center color
                        Color(0xFFB3B0AE), // Outer color
                        Color(0xC4D1D1D0)  // Adding a lighter color for more depth
                    ),
                    center = Offset(0.7f, 0.5f), // Center point in the middle
                    radius = 1000f // Radius of the gradient
                )
            )
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
                                Log.d(
                                    "ImageLoading",
                                    "Successfully opened input stream for $imageUri"
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("ImageLoading", "Error opening input stream for $imageUri", e)
                        }
                    }
                )
            }
        }
        else{
            if(post.caption != null){
                Text(text = post.caption,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
        IconButton(onClick = {
            navController.navigate(Routes.editPost+"/${post.postId}")
        },
            modifier = Modifier
                .padding(10.dp)
                .background(
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(15.dp)
                )
                .align(Alignment.TopEnd)
            ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription ="edit icon" )
        }
    }
}