package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqubTopBar(
    title: String = "",
    onBack: (() -> Unit)? = null,
    rightIcon: ImageVector? = null,
    onRightAction: (() -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = EqubTextPrimary
) {
    TopAppBar(
        title = {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = contentColor,
                        letterSpacing = (-0.2).sp
                    ),
                    modifier = Modifier.testTag("top_bar_title")
                )
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        actions = {
            if (rightContent != null) {
                rightContent()
            } else if (rightIcon != null && onRightAction != null) {
                IconButton(
                    onClick = onRightAction,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("top_bar_right_action")
                ) {
                    Icon(
                        imageVector = rightIcon,
                        contentDescription = "Action",
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor
        ),
        windowInsets = WindowInsets.statusBars
    )
}

@Composable
fun EqubStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status.lowercase()) {
        "paid" -> Triple(EqubPaidGreenLight, EqubPaidGreen, Icons.Default.CheckCircle)
        "pending", "pending..." -> Triple(EqubPendingYellowLight, EqubPendingYellow, Icons.Default.Schedule)
        "rejected" -> Triple(EqubRejectedRedLight, EqubRejectedRed, Icons.Default.Cancel)
        "upcoming" -> Triple(Color(0xFFF1F1F8), EqubTextSecondary, Icons.Default.CalendarToday)
        else -> Triple(EqubPrimaryContainer, EqubPrimary, null)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = status,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                letterSpacing = 0.1.sp
            )
        }
    }
}

@Composable
fun EqubAvatar(
    name: String,
    modifier: Modifier = Modifier.size(44.dp),
    bgColor: Color = EqubPrimary
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "E" }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun EqubButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSecondary: Boolean = false,
    isOutlined: Boolean = false,
    testTag: String = "equb_primary_button"
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = EqubPrimary,
                disabledContentColor = EqubTextTertiary
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (enabled) EqubPrimary else EqubBorder
                ),
                width = 1.5.dp
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag(testTag)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            )
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSecondary) EqubButtonSecondary else EqubPrimary,
                contentColor = if (isSecondary) EqubButtonSecondaryText else Color.White,
                disabledContainerColor = Color(0xFFE8E8EE),
                disabledContentColor = EqubTextTertiary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = if (isSecondary) 0.dp else 2.dp,
                pressedElevation = 0.dp
            ),
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag(testTag)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Composable
fun equbTextFieldColors(
    containerColor: Color = Color.White,
    textColor: Color = EqubTextPrimary
): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = EqubTextTertiary,
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        disabledContainerColor = containerColor,
        focusedBorderColor = EqubPrimary,
        unfocusedBorderColor = EqubBorder,
        disabledBorderColor = EqubBorder,
        focusedLabelColor = EqubPrimary,
        unfocusedLabelColor = EqubTextSecondary,
        focusedPlaceholderColor = EqubTextTertiary,
        unfocusedPlaceholderColor = EqubTextTertiary,
        cursorColor = EqubPrimary,
        focusedLeadingIconColor = EqubPrimary,
        unfocusedLeadingIconColor = EqubTextSecondary,
        focusedTrailingIconColor = EqubPrimary,
        unfocusedTrailingIconColor = EqubTextSecondary
    )
}

