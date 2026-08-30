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
import java.text.NumberFormat
import java.util.Locale

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
    val primaryActiveEqub = joinedEqubs.firstOrNull() ?: equbs.firstOrNull()

    // Calculate total birr across all joined equb savings
    val totalSavingsSum = if (joinedEqubs.isNotEmpty()) {
        joinedEqubs.sumOf { item ->
            item.userSavedAmount
                .replace("ETB", "", ignoreCase = true)
                .replace("Birr", "", ignoreCase = true)
                .replace(",", "")
                .trim()
                .toIntOrNull() ?: 20000
        }
    } else {
        20000
    }

    val formattedTotalSavings = NumberFormat.getNumberInstance(Locale.US).format(totalSavingsSum)

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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 28.dp)
        ) {
            // 1. Welcome Section (Centered)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
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

            // 2. Hero: Total Savings Card (Aggregated Birr across all savings)
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
                            .padding(vertical = 22.dp, horizontal = 20.dp),
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Display Amount + Currency
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = formattedTotalSavings,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = EqubPrimary,
                                letterSpacing = (-0.5).sp,
                                modifier = Modifier.testTag("home_total_savings_amount")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ETB",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EqubPrimary.copy(alpha = 0.85f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Next Payment Pill Badge
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF5EB),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE3B3)),
                            modifier = Modifier.clickable { onNavigateToActiveEqub() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = "Calendar",
                                    tint = EqubPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Next Payment: ${primaryActiveEqub?.nextPaymentDate ?: "12th Oct"} (5 days left)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EqubTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 3. Where You Saved Section (Breakdown showing where you saved & how much you saved)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Where You Saved",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .testTag("home_where_you_saved_title")
                        )
                        Text(
                            text = "${joinedEqubs.size} Active Equb",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = EqubTextSecondary
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFFE2E8F8), RoundedCornerShape(20.dp))
                            .clickable { onNavigateToActiveEqub() }
                            .testTag("home_where_you_saved_card"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            val activeItem = primaryActiveEqub ?: EqubItem(
                                id = "equb-car",
                                title = "Weekly Car Fund",
                                totalAmount = "250,000 ETB",
                                monthlyContribution = "5,000 ETB",
                                currentMembers = 15,
                                maxMembers = 15,
                                durationMonths = 12,
                                nextPaymentDate = "12th Oct",
                                category = "Auto",
                                goalAmount = "250,000 ETB",
                                progressAmount = "100,000 ETB",
                                dueDate = "12th Oct",
                                isUserJoined = true,
                                userPosition = 4,
                                currentRound = 4,
                                totalRounds = 12,
                                userSavedAmount = "20,000 ETB"
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFFFFE3B3)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.DirectionsCar,
                                            contentDescription = "Car",
                                            tint = EqubPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = activeItem.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EqubTextPrimary
                                        )
                                        Text(
                                            text = "Round ${activeItem.currentRound} of ${activeItem.totalRounds} • ${activeItem.monthlyContribution}/cycle",
                                            fontSize = 12.sp,
                                            color = EqubTextSecondary
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = activeItem.userSavedAmount,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EqubPrimary
                                    )
                                    Text(
                                        text = "Saved by you",
                                        fontSize = 11.sp,
                                        color = EqubTextSecondary
                                    )
                                }
                            }

                            // Progress Bar for current Equb rounds
                            val progressFraction = (activeItem.currentRound.toFloat() / activeItem.totalRounds.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Equb Progress",
                                        fontSize = 12.sp,
                                        color = EqubTextSecondary
                                    )
                                    Text(
                                        text = "${(progressFraction * 100).toInt()}% completed",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EqubPrimary
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape),
                                    color = EqubPrimary,
                                    trackColor = Color(0xFFF7E6DC)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Single Action Button: "Explore Equbs"
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToDiscover() }
                        .testTag("home_explore_equbs_button"),
                    colors = CardDefaults.cardColors(containerColor = EqubPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Explore,
                            contentDescription = "Explore Equbs",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Explore Equbs",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // 5. Section: My Managed Equb Overview
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Equb",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .testTag("home_my_equbs_title")
                        )
                        TextButton(onClick = onNavigateToActiveEqub) {
                            Text(
                                text = "Manage Details",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EqubPrimary
                            )
                        }
                    }

                    // Unified Container Card for the single managed Equb
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFFE2E8F8), RoundedCornerShape(20.dp))
                            .clickable { onNavigateToActiveEqub() }
                            .testTag("home_my_equb_card"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        val activeItem = primaryActiveEqub ?: EqubItem(
                            id = "equb-car",
                            title = "Weekly Car Fund",
                            totalAmount = "250,000 ETB",
                            monthlyContribution = "5,000 ETB",
                            currentMembers = 15,
                            maxMembers = 15,
                            durationMonths = 12,
                            nextPaymentDate = "12th Oct",
                            category = "Auto",
                            goalAmount = "250,000 ETB",
                            progressAmount = "100,000 ETB",
                            dueDate = "12th Oct",
                            isUserJoined = true,
                            userPosition = 4,
                            currentRound = 4,
                            totalRounds = 12,
                            userSavedAmount = "20,000 ETB"
                        )

                        EqubProgressRowItem(
                            title = activeItem.title,
                            percentage = "${((activeItem.currentRound.toFloat() / activeItem.totalRounds.coerceAtLeast(1)) * 100).toInt()}%",
                            progress = (activeItem.currentRound.toFloat() / activeItem.totalRounds.coerceAtLeast(1)).coerceIn(0f, 1f),
                            icon = Icons.Outlined.DirectionsCar,
                            iconBgColor = Color(0xFFFFE3B3),
                            progressColor = EqubPrimary,
                            onClick = onNavigateToActiveEqub
                        )
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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = progressColor,
                modifier = Modifier.size(22.dp)
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
                    fontWeight = FontWeight.SemiBold,
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
                trackColor = Color(0xFFF7E6DC)
            )
        }
    }
}
