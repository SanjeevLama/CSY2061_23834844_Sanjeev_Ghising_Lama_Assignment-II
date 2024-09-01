package com.example.myassignmenttwo.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myassignmenttwo.databaseManagement.DatabaseHelper
import com.example.myassignmenttwo.databaseManagement.User
import com.example.myassignmenttwo.navigation.Routes

@Composable
fun AdminHome(navController: NavController) {
    val context = LocalContext.current // Obtain the context using LocalContext
    val dbHelper = DatabaseHelper(context)
    val users = remember {
        mutableStateOf(listOf<User>())
    }

    LaunchedEffect(key1 = Unit) {
        users.value = dbHelper.getAllUsers()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LogoTop(navController)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Students",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            itemsIndexed(users.value){index, user ->
                StudentUi(user, navController, dbHelper, users)
            }
        }
    }
}

@Composable
fun LogoTop(navController: NavController) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ){
        Text(
            text = "Stu-gram",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.Center)
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onClick = {
                navController.navigate(Routes.logout)
            }
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "logout")
        }
    }
}

@Composable
fun StudentUi(
    user: User,
    navController: NavController, dbHelper: DatabaseHelper, users: MutableState<List<User>>
) {
    Row(
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
                    center = Offset(0.7f, 0.5f), // Center point in the middle
                    radius = 1000f // Radius of the gradient
                ),
                shape = RoundedCornerShape(10.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    )
     {
        Text(
            text = user.fullName,
            style = TextStyle(
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(10.dp)
        )
         Spacer(modifier = Modifier.weight(1f))
         Row{
             IconButton(onClick = {
                 navController.navigate(Routes.editStudent+"/${user.userId}")
             }) {
                 Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit icon")
             }
             IconButton(onClick = {
                 dbHelper.deleteUser(user.userId.toLong())
                 dbHelper.deletePost(user.userId.toLong())
                 users.value = dbHelper.getAllUsers()
             }) {
                 Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete icon")
             }
         }

    }
}
