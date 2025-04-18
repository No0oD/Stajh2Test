package com.example.stajh2test.ui.screens.features

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stajh2test.Model.time.AddReminderDialog
import com.example.stajh2test.ui.components.ReminderItem
import com.example.stajh2test.ui.states.Reminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFun3Screen(navController: NavController) {
    // State for reminders list
    val reminders = remember { mutableStateListOf<Reminder>() }

    // State for showing add dialog
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (reminders.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reminders yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // List of reminders
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(reminders) { index, reminder ->
                        ReminderItem (
                            reminder = reminder,
                            onDeleteClick = {
                                reminders.removeAt(index)
                            }
                        )
                    }
                }
            }

            // Show add reminder dialog if needed
            if (showAddDialog) {
                ModalBottomSheet(
                    onDismissRequest = { showAddDialog = false }
                ) {
                    AddReminderDialog(
                        onDismiss = { showAddDialog = false },
                        onAddReminder = { reminder ->
                            reminders.add(reminder)
                        }
                    )
                }
            }
        }
    }
}
