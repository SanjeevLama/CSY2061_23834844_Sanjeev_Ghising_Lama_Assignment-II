package com.example.myassignmenttwo.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myassignmenttwo.R
import com.example.myassignmenttwo.databaseManagement.DatabaseHelper
import com.example.myassignmenttwo.navigation.SharedViewModel
import com.example.myassignmenttwo.permission.RequestPermission

@Composable
fun EditPost(viewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), postId: Int, navController: NavController) {
    val currentUserId = viewModel.userId.value

    val context = LocalContext.current // Obtain the context using LocalContext
    val dbHelper = DatabaseHelper(context)
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        Text(
            text = "Post Detail",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (currentUserId != null) {
            EditPostUi(currentUserId.toInt(), dbHelper, postId, context, navController)
        }
    }
}

@Composable
fun EditPostUi(
    currentUser: Int,
    dbHelper: DatabaseHelper,
    postId: Int,
    context: Context,
    navController: NavController
) {
    val userName = dbHelper.getUserNameById(currentUser) // Retrieve user name from user id

    // Retrieve post details
    val post = dbHelper.getPostById(postId)

    var contentText by remember { mutableStateOf(post?.caption) }
    var selectedImageUri by remember { mutableStateOf(post?.imageUri) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri?.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x682B2B2B), // Center color
                        Color(0xFFB3B0AE), // Outer color
                        Color(0xC4D1D1D0)  // Adding a lighter color for more depth
                    ),
                    center = Offset(0.5f, 0.5f), // Center point in the middle
                    radius = 1000f // Radius of the gradient
                ),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            modifier = Modifier
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
        ) {
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
                Text(
                    text = userName,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }
        }

        contentText?.let { it ->
            TextField(
                value = it,
                onValueChange = { contentText = it },
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(150.dp)
            )
        }

        if (selectedImageUri != null) {
            val imageUri = remember(selectedImageUri) {
                Uri.parse(selectedImageUri)
            }

            Log.d("ImageLoading", "Attempting to load image with URI: $imageUri")

            RequestPermission(
                permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
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
                        .padding(10.dp)
                        .height(200.dp)
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
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    selectedImageUri = null
                }) {
                    Text("Remove photo")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (contentText != null || selectedImageUri != null) {
                            dbHelper.updatePost(postId, contentText, selectedImageUri)
                            navController.popBackStack()
                            showToast(context, "Post updated successfully")
                        } else {
                            showToast(context, "Please write something or upload a photo")
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78A7CF))
                ) {
                    Text("SAVE")
                }
            }
        } else {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_photo_alternate_24),
                    contentDescription = "add photo",
                    Modifier
                        .size(40.dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (contentText != null || selectedImageUri != null) {
                            dbHelper.updatePost(postId, contentText, selectedImageUri)
                            navController.popBackStack()
                            showToast(context, "Post updated successfully")
                        } else {
                            showToast(context, "Please write something or upload a photo")
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78A7CF))
                ) {
                    Text("SAVE")
                }
            }
        }
    }
}



