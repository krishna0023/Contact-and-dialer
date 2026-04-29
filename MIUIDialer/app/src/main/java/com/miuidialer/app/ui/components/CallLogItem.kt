package com.miuidialer.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miuidialer.app.data.model.CallLogEntry
import com.miuidialer.app.data.model.CallType
import com.miuidialer.app.ui.theme.MIUIRed
import com.miuidialer.app.ui.theme.MIUITextPrimary
import com.miuidialer.app.ui.theme.MIUITextSecondary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallLogItem(
    entry: CallLogEntry,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(
            name = entry.name?.takeIf { it.isNotEmpty() } ?: entry.number,
            size = 44.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.name?.takeIf { it.isNotEmpty() } ?: entry.number,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (entry.type == CallType.MISSED) MIUIRed else MIUITextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (entry.type) {
                        CallType.INCOMING -> Icons.Default.CallReceived
                        CallType.OUTGOING -> Icons.Default.CallMade
                        CallType.MISSED -> Icons.Default.CallMissed
                        CallType.REJECTED -> Icons.Default.CallMissedOutgoing
                    },
                    contentDescription = null,
                    tint = when (entry.type) {
                        CallType.MISSED, CallType.REJECTED -> MIUIRed
                        else -> MIUITextSecondary
                    },
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDate(entry.date),
                    fontSize = 12.sp,
                    color = MIUITextSecondary
                )
                if (entry.name != null && entry.name.isNotEmpty()) {
                    Text(
                        text = "  ${entry.number}",
                        fontSize = 12.sp,
                        color = MIUITextSecondary
                    )
                }
            }
        }
        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Info",
                tint = MIUITextSecondary
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        diff < 172_800_000 -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
