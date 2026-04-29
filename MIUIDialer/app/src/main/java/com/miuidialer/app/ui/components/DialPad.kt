package com.miuidialer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miuidialer.app.ui.theme.MIUITextPrimary
import com.miuidialer.app.ui.theme.MIUITextSecondary

private val dialKeys = listOf(
    Triple("1", "", false),
    Triple("2", "ABC", false),
    Triple("3", "DEF", false),
    Triple("4", "GHI", false),
    Triple("5", "JKL", false),
    Triple("6", "MNO", false),
    Triple("7", "PQRS", false),
    Triple("8", "TUV", false),
    Triple("9", "WXYZ", false),
    Triple("*", "", false),
    Triple("0", "+", false),
    Triple("#", "", false)
)

@Composable
fun DialPad(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteAll: () -> Unit,
    showDeleteButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        dialKeys.chunked(3).forEachIndexed { rowIdx, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { (digit, sub, _) ->
                    DialKey(
                        digit = digit,
                        sub = sub,
                        onClick = { onKeyPress(digit) },
                        onLongClick = if (digit == "0") ({ onKeyPress("+") }) else null
                    )
                }
            }
        }
        if (showDeleteButton) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Delete",
                        tint = MIUITextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DialKey(
    digit: String,
    sub: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = digit,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = MIUITextPrimary
            )
            if (sub.isNotEmpty()) {
                Text(
                    text = sub,
                    fontSize = 9.sp,
                    color = MIUITextSecondary,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
