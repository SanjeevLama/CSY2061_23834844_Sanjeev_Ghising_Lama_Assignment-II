package com.example.myassignmenttwo.screen

import android.net.Uri
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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.myassignmenttwo.R
import com.example.myassignmenttwo.databaseManagement.DatabaseHelper
import com.example.myassignmenttwo.navigation.Routes
import com.example.myassignmenttwo.navigation.SharedViewModel


@Composable
fun Create(
    navController: NavController,
    viewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val context = LocalContext.current // Obtain the context using LocalContext
    val dbHelper = DatabaseHelper(context) // Initialize DatabaseHelper here

    val userId = viewModel.userId.value
    val userName = userId?.let { dbHelper.getUserNameById(it.toInt()) }


    var contentText by remember {
        mutableStateOf("")
    }
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }


    var errorMessage by remember {
        mutableStateOf("")
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                    center = Offset(0.5f, 0.5f), // Center point in the middle
                    radius = 1000f // Radius of the gradient
                ),
                shape = RoundedCornerShape(10.dp)
            )
        ){
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
                    if (userName != null) {
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
            }

            TextField(
                value = contentText,
                onValueChange = { contentText = it },
                textStyle = TextStyle(fontSize = 16.sp),
                placeholder = { Text(text = "What's on Your Mind ?")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(200.dp)
            )

            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            }

            Row(modifier = Modifier.padding(10.dp),
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
                Button(onClick = {
                    if(contentText.isNotEmpty() || selectedImageUri != null){
                        if (userId != null) {
                            dbHelper.addPost(contentText, userId, selectedImageUri?.toString())
                        }
                        navController.navigate(Routes.home)
                    }
                    else{
                        errorMessage = "Please write something or upload a photo"
                    }
                },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF78A7CF)
                    )
                ) {
                    Text(text = "POST")
                }
            }
        }

        // Show error message if any
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

