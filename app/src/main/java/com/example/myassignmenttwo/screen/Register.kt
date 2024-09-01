package com.example.myassignmenttwo.screen


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myassignmenttwo.databaseManagement.DatabaseHelper
import com.example.myassignmenttwo.navigation.Routes
import com.example.myassignmenttwo.navigation.SharedViewModel

@Composable
fun Register(navController: NavController, sharedViewModel: SharedViewModel){

    val context = LocalContext.current // Obtain the context using LocalContext
    val dbHelper = DatabaseHelper(context) // Initialize DatabaseHelper here

    var fullName by remember{
        mutableStateOf("")
    }
    var email by remember{
        mutableStateOf("")
    }
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

    var password by remember {
        mutableStateOf("")
    }
    var dateOfBirth by remember {
        mutableStateOf("")
    }
    val datePattern = """\d{4}-\d{2}-\d{2}""".toRegex()

    var errorMessage by remember {
        mutableStateOf("")
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Register",
            style = TextStyle(
                fontSize = 30.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            Modifier
                .border(width = (1.dp), color = Color.Black, shape = RoundedCornerShape(10.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(text = "Full Name") }
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = {
                    dateOfBirth = it
                },
                label = { Text(text = "Date of birth (yyyy-mm-dd)") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") }
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                if(fullName.isNotEmpty() && email.isNotEmpty() && dateOfBirth.isNotEmpty() && password.isNotEmpty()){
                    if(!email.matches(emailPattern)){
                        errorMessage = "Invalid email format."
                    }
                    else if (!dateOfBirth.matches(datePattern)){
                        errorMessage = "Invalid date format."
                    }
                    else if (dbHelper.emailExists(email)) {
                        errorMessage = "Email already exists."
                    }
                    else{
                        val userId = dbHelper.addUser(fullName, email, dateOfBirth, password)
                        sharedViewModel.setUserId(userId)
                        navController.navigate(Routes.home){
                            popUpTo(Routes.login) { inclusive = true }
                        }
                    }
                }
                else{
                    errorMessage = "Please fill in all fields."
                }
            },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF48aaff)
                )
            ) {
                Text(text = "Register")
            }
        }

        // Show error message if any
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(fontSize = 14.sp)
            )
        }


        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Already hava an account?")
            TextButton(onClick = { navController.navigate(Routes.login) }
            ) {
                Text(text = "Login", style = TextStyle(color = Color(0xFF48aaff)))
            }
        }

    }
}


