package com.example.myassignmenttwo.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myassignmenttwo.databaseManagement.DatabaseHelper
import com.example.myassignmenttwo.navigation.Routes

@Composable
fun AddStudent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LogoTop(navController)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Add Student",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        AddStudentUi(navController)
    }
}

@Composable
fun AddStudentUi(navController: NavController) {
    val context = LocalContext.current // Obtain the context using LocalContext
    val dbHelper = DatabaseHelper(context)

    val datePattern = """\d{4}-\d{2}-\d{2}""".toRegex()
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

    Column {
        var fullName by remember{
            mutableStateOf("")
        }
        var dateOfBirth by remember {
            mutableStateOf("")
        }
        var email by remember{
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(text = "Full Name") }
                )
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = {
                        dateOfBirth = it
                    },
                    label = { Text(text = "Date of birth (yyyy-mm-dd)") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Button(onClick = {
                        navController.navigate(Routes.adminHome)
                    },
                        modifier = Modifier.width(95.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF70707)
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                    Button(onClick = {
                        if(fullName.isNotEmpty() && email.isNotEmpty() && dateOfBirth.isNotEmpty() && password.isNotEmpty()){
                            if(!email.matches(emailPattern)){
                                showToast(context, "Invalid email format.")
                            }
                            else if (!dateOfBirth.matches(datePattern)){
                                showToast(context, "Invalid date format.")
                            }
                            else if (dbHelper.emailExists(email)) {
                                showToast(context, "Email already exists.")
                            }
                            else{
                                dbHelper.addUser(fullName, email, dateOfBirth, password)
                                navController.navigate(Routes.adminHome)
                            }
                        }
                        else{
                            showToast(context, "Please fill in all fields.")
                        }
                    },
                        modifier = Modifier.width(95.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF48aaff)
                        )
                    ) {
                        Text(text = "Add")
                    }
                }
            }
        }
    }
}
