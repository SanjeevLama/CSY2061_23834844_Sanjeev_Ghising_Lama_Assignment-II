package com.example.myassignmenttwo.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myassignmenttwo.screen.AboutUser
import com.example.myassignmenttwo.screen.AddStudent
import com.example.myassignmenttwo.screen.AdminHome
import com.example.myassignmenttwo.screen.Comment
import com.example.myassignmenttwo.screen.Create
import com.example.myassignmenttwo.screen.EditPost
import com.example.myassignmenttwo.screen.EditStudent
import com.example.myassignmenttwo.screen.Home
import com.example.myassignmenttwo.screen.Login
import com.example.myassignmenttwo.screen.Logout
import com.example.myassignmenttwo.screen.Register


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenNavigation() {

    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home, Routes.home),
        NavItem("Create", Icons.Default.AddCircle, Routes.create),
        NavItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, Routes.logout)
    )

    var selectedIcon by remember {
        mutableIntStateOf(0)
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Update selected icon based on current route
    LaunchedEffect(currentRoute) {
        selectedIcon = navItemList.indexOfFirst { it.route == currentRoute }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute != Routes.login &&
                currentRoute != Routes.register &&
                currentRoute != Routes.comment+"/{postId}" &&
                currentRoute != Routes.adminHome &&
                currentRoute != Routes.addStudent &&
                currentRoute != Routes.editStudent+"/{userId}" &&
                currentRoute != Routes.aboutUser &&
                currentRoute != Routes.editPost
            ) {
                NavigationBar {
                    navItemList.forEachIndexed { index, navItem ->
                        NavigationBarItem(
                            selected = selectedIcon == index,
                            onClick = {
                                selectedIcon = index
                                navController.navigate(navItem.route) {
                                    // Avoid re-adding the same destination to the back stack
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(imageVector = navItem.icon, contentDescription = "icon")
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == Routes.adminHome) {
                FloatingActionButton(
                    modifier = Modifier.size(70.dp),
                    onClick = {
                        navController.navigate(Routes.addStudent)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Icon",
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Routes.login
        ) {
            composable(Routes.login) {
                Login(navController, sharedViewModel)
            }
            composable(Routes.register) {
                Register(navController, sharedViewModel)
            }
            composable(Routes.home) {
                Home(sharedViewModel, navController)
            }
            composable(Routes.create) {
                Create(navController, sharedViewModel)
            }
            composable(Routes.logout) {
                Logout(navController, sharedViewModel)
            }
            composable(Routes.comment + "/{postId}") {
                val postId = it.arguments?.getString("postId")?.toInt()
                if (postId != null) {
                    Comment(postId, navController, sharedViewModel)
                }
            }
            composable(Routes.aboutUser) {
                AboutUser(sharedViewModel, navController)
            }
            composable(Routes.editPost + "/{postId}") {
                val postId = it.arguments?.getString("postId")?.toInt()
                if (postId != null) {
                    EditPost(sharedViewModel, postId, navController)
                }
            }
            // Admin part
            composable(Routes.adminHome) {
                AdminHome(navController)
            }
            composable(Routes.addStudent) {
                AddStudent(navController)
            }
            composable(Routes.editStudent + "/{userId}") {
                val studentId = it.arguments?.getString("userId")?.toInt()
                if (studentId != null) {
                    EditStudent(studentId, navController)
                }
            }
        }
    }
}

