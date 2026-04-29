package com.miuidialer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miuidialer.app.ui.components.CallLogItem
import com.miuidialer.app.ui.components.DialPad
import com.miuidialer.app.ui.theme.*
import com.miuidialer.app.viewmodel.DialerViewModel

@Composable
fun DialerScreen(
    viewModel: DialerViewModel = hiltViewModel(),
    onNavigateToContacts: () -> Unit
) {
    val context = LocalContext.current
    val dialInput by viewModel.dialInput.collectAsState()
    val callLog by viewModel.callLog.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showDialPad by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showDialPad) "" else "Recents",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /* settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MIUITextSecondary)
            }
        }

        // Search bar
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
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 15.sp, color = MIUITextPrimary),
                decorationBox = { inner ->
                    if (searchQuery.isEmpty()) {
                        Text("Search contacts", fontSize = 15.sp, color = MIUITextSecondary)
                    }
                    inner()
                }
            )
        }

        if (!showDialPad) {
            // Call log list
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                if (searchQuery.isNotEmpty()) {
                    val filtered by viewModel.filteredContacts.collectAsState()
                    items(filtered) { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    contact.phoneNumbers.firstOrNull()?.let {
                                        viewModel.makeCall(context, it.number)
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            com.miuidialer.app.ui.components.ContactAvatar(name = contact.name, size = 44.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(contact.name, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    contact.phoneNumbers.firstOrNull()?.number ?: "",
                                    fontSize = 12.sp,
                                    color = MIUITextSecondary
                                )
                            }
                        }
                        Divider(color = MIUIDivider, thickness = 0.5.dp, modifier = Modifier.padding(start = 72.dp))
                    }
                } else {
                    items(callLog) { entry ->
                        CallLogItem(
                            entry = entry,
                            onClick = { viewModel.makeCall(context, entry.number) },
                            onInfoClick = { viewModel.setDialInput(entry.number); showDialPad = true }
                        )
                        Divider(color = MIUIDivider, thickness = 0.5.dp, modifier = Modifier.padding(start = 72.dp))
                    }
                }
            }
        } else {
            // Dial input display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dialInput,
                    fontSize = when {
                        dialInput.length > 12 -> 24.sp
                        dialInput.length > 8 -> 30.sp
                        else -> 38.sp
                    },
                    fontWeight = FontWeight.Light,
                    color = MIUITextPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
                if (dialInput.isNotEmpty()) {
                    IconButton(onClick = viewModel::onDialPadDelete) {
                        Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = MIUITextSecondary)
                    }
                }
            }

            // Dial pad
            DialPad(
                onKeyPress = viewModel::onDialPadPress,
                onDelete = viewModel::onDialPadDelete,
                onDeleteAll = viewModel::onDialPadDeleteAll,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Dual SIM call buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // SIM 1
                Button(
                    onClick = { if (dialInput.isNotEmpty()) viewModel.makeCall(context, dialInput, 0) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MIUIGreen),
                    enabled = dialInput.isNotEmpty()
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SIM 1", fontWeight = FontWeight.Medium)
                }
                // SIM 2
                Button(
                    onClick = { if (dialInput.isNotEmpty()) viewModel.makeCall(context, dialInput, 1) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MIUIGreen),
                    enabled = dialInput.isNotEmpty()
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SIM 2", fontWeight = FontWeight.Medium)
                }
            }
        }

        // Bottom nav bar
        HorizontalDivider(color = MIUIDivider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Menu / hamburger
            IconButton(onClick = { }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MIUITextSecondary)
            }
            // Recents tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { showDialPad = false }
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Call,
                    contentDescription = "Recents",
                    tint = if (!showDialPad) MIUIGreen else MIUITextSecondary
                )
                Text(
                    "Recents",
                    fontSize = 11.sp,
                    color = if (!showDialPad) MIUIGreen else MIUITextSecondary
                )
            }
            // Dial pad toggle (center)
            IconButton(onClick = { showDialPad = !showDialPad }) {
                Icon(
                    Icons.Default.DialerSip,
                    contentDescription = "Dialpad",
                    tint = if (showDialPad) MIUIGreen else MIUITextSecondary
                )
            }
            // Contacts tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigateToContacts() }
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = "Contacts", tint = MIUITextSecondary)
                Text("Contacts", fontSize = 11.sp, color = MIUITextSecondary)
            }
        }
    }
}
