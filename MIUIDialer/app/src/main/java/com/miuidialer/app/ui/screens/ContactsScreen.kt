package com.miuidialer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miuidialer.app.data.model.Contact
import com.miuidialer.app.ui.components.ContactAvatar
import com.miuidialer.app.ui.theme.*
import com.miuidialer.app.viewmodel.ContactsViewModel
import com.miuidialer.app.viewmodel.DialerViewModel

@Composable
fun ContactsScreen(
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    dialerViewModel: DialerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val contacts by contactsViewModel.filtered.collectAsState()
    val searchQuery by contactsViewModel.searchQuery.collectAsState()
    val isLoading by contactsViewModel.isLoading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Contacts",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add contact")
            }
        }

        // Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MIUILightGray)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = MIUITextSecondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = searchQuery,
                onValueChange = contactsViewModel::search,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 15.sp, color = MIUITextPrimary),
                decorationBox = { inner ->
                    if (searchQuery.isEmpty()) Text("Search", fontSize = 15.sp, color = MIUITextSecondary)
                    inner()
                }
            )
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MIUIGreen)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                val grouped = contacts.groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }
                grouped.toSortedMap().forEach { (letter, group) ->
                    item {
                        Text(
                            text = letter.toString(),
                            fontSize = 12.sp,
                            color = MIUITextSecondary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                        )
                    }
                    items(group) { contact ->
                        ContactItem(
                            contact = contact,
                            onCall = { number -> dialerViewModel.makeCall(context, number) }
                        )
                        HorizontalDivider(
                            color = MIUIDivider,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 72.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone ->
                contactsViewModel.addContact(name, phone) { showAddDialog = false }
            }
        )
    }
}

@Composable
private fun ContactItem(
    contact: Contact,
    onCall: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (contact.phoneNumbers.size == 1) {
                        onCall(contact.phoneNumbers.first().number)
                    } else {
                        expanded = !expanded
                    }
                }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(name = contact.name, size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(contact.name, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MIUITextPrimary)
                Text(
                    contact.phoneNumbers.firstOrNull()?.number ?: "",
                    fontSize = 12.sp,
                    color = MIUITextSecondary
                )
            }
            IconButton(onClick = { onCall(contact.phoneNumbers.firstOrNull()?.number ?: "") }) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = MIUIGreen)
            }
        }
        if (expanded && contact.phoneNumbers.size > 1) {
            contact.phoneNumbers.drop(1).forEach { phone ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCall(phone.number) }
                        .padding(start = 72.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(phone.type, fontSize = 11.sp, color = MIUITextSecondary, modifier = Modifier.width(48.dp))
                    Text(phone.number, fontSize = 14.sp, color = MIUITextPrimary, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = MIUIGreen, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New contact") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank() && phone.isNotBlank()) onAdd(name, phone) },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) { Text("Save", color = MIUIGreen) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
