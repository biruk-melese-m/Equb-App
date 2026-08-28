package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EqubRepository
import com.example.data.PaymentRound
import com.example.ui.components.EqubAvatar
import com.example.ui.components.EqubStatusBadge
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun EqubPaymentScheduleScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule by EqubRepository.paymentSchedule.collectAsState()
    var selectedTab by remember { mutableStateOf("All") }

    val filteredSchedule = schedule.filter {
        selectedTab == "All" || it.status.equals(selectedTab, ignoreCase = true)
    }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Equb Payment Schedule",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("payment_schedule_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Segmented Tabs: All | Paid | Pending | Upcoming
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBEBF5))
                    .padding(4.dp)
            ) {
                listOf("All", "Paid", "Pending", "Upcoming").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) EqubTextPrimary else EqubTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredSchedule) { round ->
                    PaymentScheduleCard(round = round)
                }
            }
        }
    }
}

@Composable
fun PaymentScheduleCard(
    round: PaymentRound,
    modifier: Modifier = Modifier
) {
    val isPendingHighlight = round.status == "Pending"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isPendingHighlight) 1.5.dp else 1.dp,
                color = if (isPendingHighlight) EqubPrimary else EqubCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("schedule_card_round_${round.roundNumber}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isPendingHighlight) Color(0xFFF9F7FE) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EqubAvatar(
                    name = round.recipientName,
                    modifier = Modifier.size(42.dp),
                    bgColor = if (isPendingHighlight) EqubPrimary else Color(0xFF7E57C2)
                )

                Column {
                    Text(
                        text = "Round ${round.roundNumber}: ${round.recipientName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = round.amount,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubPrimary
                    )
                }
            }

            EqubStatusBadge(status = round.status)
        }
    }
}
