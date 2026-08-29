package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.EqubItem
import com.example.data.EqubRepository
import com.example.ui.components.EqubAvatar
import com.example.ui.theme.*

@Composable
fun HomeDashboardScreen(
    onSelectEqub: (EqubItem) -> Unit,
    onNavigateToActiveEqub: () -> Unit,
    onNavigateToSubmitPayment: () -> Unit,
    onNavigateToDiscover: () -> Unit,
    onNavigateToMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by EqubRepository.userProfile.collectAsState()
    val equbs by EqubRepository.equbs.collectAsState()
    val joinedEqubs = equbs.filter { it.isUserJoined }

    // Parse numeric total savings if possible (e.g., "20,000 ETB" -> "20,000")
    val savingsAmountText = userProfile.totalSavings
        .replace("ETB", "")
        .replace("etb", "")
        .trim()
        .ifEmpty { "20,000" }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            // Top App Bar: Avatar (Left), EqubHub (Center), Notifications (Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar on Left
                EqubAvatar(
                    name = userProfile.fullName.ifEmpty { "Brook Melles" },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("home_profile_avatar")
                )

                // App Title Centered
                Text(
                    text = "EqubHub",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = EqubPrimary,
                    letterSpacing = (-0.4).sp,
                    modifier = Modifier.testTag("home_header_title")
                )

                // Notification Bell Icon on Right
                IconButton(
                    onClick = onNavigateToMessages,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .testTag("home_notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = EqubTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        modifier = modifier.testTag("home_dashboard_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp)
        ) {
            // 1. Welcome Section (Centered)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome back, ${userProfile.name} \uD83D\uDC4B",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary,
                        letterSpacing = (-0.3).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("home_greeting_text")
                    )
                }
            }

            // 2. Hero: Total Savings Card (Ultra-Minimalist)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFFE2E8F8), RoundedCornerShape(24.dp))
                        .testTag("home_savings_hero_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Savings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = EqubTextSecondary,
                            modifier = Modifier.testTag("home_total_savings_label")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Display Amount + Currency
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = savingsAmountText,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = EqubPrimary,
                                letterSpacing = (-0.5).sp,
                                modifier = Modifier.testTag("home_total_savings_amount")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ETB",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EqubPrimary.copy(alpha = 0.85f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Next Payment Pill Badge
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF0F3FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCFBDFF)),
                            modifier = Modifier.clickable { onNavigateToActiveEqub() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = "Calendar",
                                    tint = EqubTextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Next Payment: 12th Oct",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EqubTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 3. Quick Actions (+ Start, Join)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Start Button
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFCBC3D9), RoundedCornerShape(12.dp))
                            .clickable { onNavigateToDiscover() }
                            .testTag("home_start_button"),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddCircleOutline,
                                contentDescription = "Start",
                                tint = EqubTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EqubTextPrimary
                            )
                        }
                    }

                    // Join Button
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFCBC3D9), RoundedCornerShape(12.dp))
                            .clickable { onNavigateToDiscover() }
                            .testTag("home_join_button"),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.GroupAdd,
                                contentDescription = "Join",
                                tint = EqubTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Join",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EqubTextPrimary
                            )
                        }
                    }
                }
            }

            // 4. Section: My Equbs
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "My Equbs",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .testTag("home_my_equbs_title")
                    )

                    // Unified Container Card with divider rows
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFFE2E8F8), RoundedCornerShape(20.dp))
                            .testTag("home_my_equbs_container"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Display the joined equbs or standard items matching the design
                            val itemsToDisplay = if (joinedEqubs.isNotEmpty()) joinedEqubs else equbs.take(3)

                            itemsToDisplay.forEachIndexed { index, equb ->
                                val (icon, iconBg, progressColor, progressFraction, percentageText) = when {
                                    equb.title.contains("Car", ignoreCase = true) || equb.category.contains("Auto", ignoreCase = true) -> {
                                        Tuple5(
                                            Icons.Outlined.DirectionsCar,
                                            Color(0xFFE8DDFF),
                                            EqubPrimary,
                                            0.40f,
                                            "40%"
                                        )
                                    }
                                    equb.title.contains("Tech", ignoreCase = true) || equb.id.contains("tech", ignoreCase = true) -> {
                                        Tuple5(
                                            Icons.Outlined.LaptopMac,
                                            Color(0xFFEBDCFF),
                                            Color(0xFF6D4EA2),
                                            0.75f,
                                            "75%"
                                        )
                                    }
                                    equb.title.contains("Holiday", ignoreCase = true) || equb.title.contains("Travel", ignoreCase = true) -> {
                                        Tuple5(
                                            Icons.Outlined.FlightTakeoff,
                                            Color(0xFFFFDBCF),
                                            Color(0xFF963200),
                                            0.15f,
                                            "15%"
                                        )
                                    }
                                    else -> {
                                        val fraction = if (index % 2 == 0) 0.50f else 0.30f
                                        Tuple5(
                                            Icons.Outlined.AccountBalanceWallet,
                                            Color(0xFFEDE8FA),
                                            EqubPrimary,
                                            fraction,
                                            "${(fraction * 100).toInt()}%"
                                        )
                                    }
                                }

                                EqubProgressRowItem(
                                    title = equb.title,
                                    percentage = percentageText,
                                    progress = progressFraction,
                                    icon = icon,
                                    iconBgColor = iconBg,
                                    progressColor = progressColor,
                                    onClick = { onSelectEqub(equb) }
                                )

                                if (index < itemsToDisplay.size - 1) {
                                    HorizontalDivider(
                                        color = Color(0xFFF0EDFA),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(start = 68.dp, end = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EqubProgressRowItem(
    title: String,
    percentage: String,
    progress: Float,
    icon: ImageVector,
    iconBgColor: Color,
    progressColor: Color,
    onClick: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700),
        label = "equbRowProgress"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = progressColor,
                modifier = Modifier.size(20.dp)
            )
        }

        // Title + Progress Bar + Percentage
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = EqubTextPrimary
                )
                Text(
                    text = percentage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            // Custom styled linear progress indicator
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = progressColor,
                trackColor = Color(0xFFDCE2F3)
            )
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
