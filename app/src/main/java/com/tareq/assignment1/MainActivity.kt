package com.tareq.assignment1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserInputScreen("225")
        }
    }
}

@Composable
fun UserInputScreen(s: String) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val usernameKey = stringPreferencesKey("username")
    val emailKey = stringPreferencesKey("email")
    val idKey = stringPreferencesKey("id")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID") }
        )
        // ... [Other Composable functions]

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Save Button
            Button(
                onClick = {
                    scope.launch {
                        saveData(context, usernameKey, username, emailKey, email, idKey, id) { msg ->
                            message = msg
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF40E0D0)) // Turquoise color
            ) {
                Text("Save", color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Load Button
            Button(
                onClick = {
                    scope.launch {
                        loadData(context, usernameKey, emailKey, idKey) { u, e, i, msg ->
                            username = u; email = e; id = i; message = msg
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue) // Custom color for Load button
            ) {
                Text("Load", color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Clear Button
            Button(
                onClick = {
                    scope.launch {
                        clearData(context, usernameKey, emailKey, idKey) {
                            message = it
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red) // Custom color for Clear button
            ) {
                Text("Clear", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

// ... [Display message and other components]


        Spacer(modifier = Modifier.height(16.dp))
        Text("Student Name: $username")
        Text("Student ID: $id")
        Text(message)
    }
}

suspend fun saveData(
    context: Context,
    usernameKey: Preferences.Key<String>,
    username: String,
    emailKey: Preferences.Key<String>,
    email: String,
    idKey: Preferences.Key<String>,
    id: String,
    updateMessage: (String) -> Unit
) {
    try {
        if (username.isBlank() || email.isBlank() || id.isBlank()) {
            updateMessage("Please fill all fields.")
            return
        }
        context.dataStore.edit { preferences ->
            preferences[usernameKey] = username
            preferences[emailKey] = email
            preferences[idKey] = id
        }
        updateMessage("Data saved successfully.")
    } catch (e: Exception) {
        updateMessage("Error saving data: ${e.localizedMessage}")
    }
}

suspend fun loadData(
    context: Context,
    usernameKey: Preferences.Key<String>,
    emailKey: Preferences.Key<String>,
    idKey: Preferences.Key<String>,
    updateUI: (String, String, String, String) -> Unit
) {
    try {
        val preferences = context.dataStore.data.first()
        val username = preferences[usernameKey] ?: ""
        val email = preferences[emailKey] ?: ""
        val id = preferences[idKey] ?: ""
        updateUI(username, email, id, "Data loaded successfully.")
    } catch (e: Exception) {
        updateUI("", "", "", "Error loading data: ${e.localizedMessage}")
    }
}

suspend fun clearData(
    context: Context,
    usernameKey: Preferences.Key<String>,
    emailKey: Preferences.Key<String>,
    idKey: Preferences.Key<String>,
    updateMessage: (String) -> Unit
) {
    context.dataStore.edit { preferences ->
        preferences.remove(usernameKey)
        preferences.remove(emailKey)
        preferences.remove(idKey)
    }
    updateMessage("Data cleared successfully.")
}
