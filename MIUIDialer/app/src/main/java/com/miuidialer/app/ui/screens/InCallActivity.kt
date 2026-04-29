package com.miuidialer.app.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miuidialer.app.service.CallManager
import com.miuidialer.app.ui.theme.MIUICallBgEnd
import com.miuidialer.app.ui.theme.MIUICallBgStart
import com.miuidialer.app.ui.theme.MIUIDialerTheme
import com.miuidialer.app.ui.theme.MIUIRed
import kotlinx.coroutines.delay

class InCallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val incomingNumber = intent.getStringExtra("incoming_number") ?: ""
        setContent {
            MIUIDialerTheme {
                InCallScreen(
                    incomingNumber = incomingNumber,
                    onEnd = { finish() }
                )
            }
        }
    }
}

@Composable
fun InCallScreen(
    incomingNumber: String = "",
    onEnd: () -> Unit = {}
) {
    val callState by CallManager.callState.collectAsState()
    val callerName = CallManager.getCallerName()
    val callerNumber = CallManager.getCallerNumber().ifEmpty { incomingNumber }
    var isMuted by remember { mutableStateOf(false) }
    var isOnHold by remember { mutableStateOf(false) }
    var isSpeaker by remember { mutableStateOf(false) }
    var callDuration by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callDuration++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MIUICallBgStart, MIUICallBgEnd, Color(0xFF1E0A4A))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Caller avatar
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE91E63)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (callerName ?: callerNumber).firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontSize = 36.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = callerName ?: callerNumber,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            if (callerName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = callerNumber,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatDuration(callDuration),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 6-button grid (MIUI style)
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InCallButton(
                        icon = Icons.Default.Videocam,
                        label = "Video call",
                        active = false,
                        onClick = {}
                    )
                    InCallButton(
                        icon = Icons.Default.AddCall,
                        label = "Add call",
                        active = false,
                        onClick = {}
                    )
                    InCallButton(
                        icon = Icons.Default.NoteAdd,
                        label = "Note",
                        active = false,
                        onClick = {}
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InCallButton(
                        icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = "Mute",
                        active = isMuted,
                        onClick = { isMuted = !isMuted }
                    )
                    InCallButton(
                        icon = Icons.Default.Pause,
                        label = "Hold",
                        active = isOnHold,
                        onClick = {
                            isOnHold = !isOnHold
                            CallManager.toggleHold()
                        }
                    )
                    InCallButton(
                        icon = Icons.Default.FiberManualRecord,
                        label = "Record",
                        active = false,
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom row: speaker | end | dialpad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (isSpeaker) 0.3f else 0.1f))
                            .clickable { isSpeaker = !isSpeaker },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isSpeaker) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "Speaker",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // End call
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(MIUIRed)
                        .clickable {
                            CallManager.disconnect()
                            onEnd()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CallEnd,
                        contentDescription = "End call",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Dialpad
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DialerSip,
                            contentDescription = "Dialpad",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InCallButton(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = if (active) 0.3f else 0.1f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
