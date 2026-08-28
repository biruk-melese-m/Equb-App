package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EqubItem
import com.example.data.EqubRepository
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

    Scaffold(
        containerColor = EqubBackground,
        modifier = modifier.testTag("home_dashboard_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            // Greeting Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hi, ${userProfile.name} 👋",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary,
                            modifier = Modifier.testTag("home_greeting_text")
                        )
                        Text(
                            text = "Welcome back to your Equbs",
                            fontSize = 14.sp,
                            color = EqubTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onNavigateToMessages,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, EqubBorder, CircleShape)
                            .testTag("home_messages_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications & Messages",
                            tint = EqubPrimary
                        )
                    }
                }
            }

            // Hero Purple Savings Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("home_savings_hero_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(EqubPurpleGradientStart, EqubPurpleGradientEnd)
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Total Savings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = userProfile.totalSavings,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.testTag("home_total_savings_amount")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Last Added Pill
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.clickable { onNavigateToSubmitPayment() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Last added: ${userProfile.lastAddedAmount} ⊕",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section: My Equbs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Equbs",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary,
                        modifier = Modifier.testTag("home_my_equbs_title")
                    )

                    Text(
                        text = "View Active Cycle",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubPrimary,
                        modifier = Modifier
                            .clickable { onNavigateToActiveEqub() }
                            .padding(4.dp)
                            .testTag("home_view_active_cycle_button")
                    )
                }
            }

            // My Equbs Horizontal Cards
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(joinedEqubs) { equb ->
                        MyEqubDashboardCard(
                            equb = equb,
                            onClick = { onSelectEqub(equb) }
                        )
                    }
                }
            }

            // Quick Actions section
            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EqubTextPrimary
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Discover Equbs",
                        icon = Icons.Outlined.Search,
                        onClick = onNavigateToDiscover,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Submit Proof",
                        icon = Icons.Outlined.UploadFile,
                        onClick = onNavigateToSubmitPayment,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MyEqubDashboardCard(
    equb: EqubItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("my_equb_card_${equb.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = equb.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EqubTextPrimary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Goal", fontSize = 12.sp, color = EqubTextSecondary)
                    Text(text = equb.goalAmount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EqubTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Progress", fontSize = 12.sp, color = EqubTextSecondary)
                    Text(text = equb.progressAmount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EqubPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            val progressFraction = if (equb.id == "equb-1") 0.25f else 0.10f
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EqubPrimary,
                trackColor = Color(0xFFF0EDFA)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Due: ${equb.dueDate}",
                fontSize = 12.sp,
                color = EqubTextSecondary
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE8FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EqubPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = EqubTextPrimary
            )
        }
    }
}
