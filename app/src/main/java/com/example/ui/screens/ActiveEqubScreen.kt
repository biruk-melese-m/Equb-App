package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun ActiveEqubScreen(
    onViewCycle: () -> Unit,
    onViewMembers: () -> Unit,
    onViewSchedule: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Active Equb",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("active_equb_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Main Active Equb Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, EqubCardBorder, RoundedCornerShape(16.dp))
                    .testTag("active_equb_main_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Monthly Savings Equb",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Contribution",
                        fontSize = 13.sp,
                        color = EqubTextSecondary
                    )

                    Text(
                        text = "5,000 ETB",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubPrimary,
                        modifier = Modifier.testTag("active_equb_contribution_text")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Progress bar 3/20 (0.15f)
                    LinearProgressIndicator(
                        progress = { 0.15f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EqubPrimary,
                        trackColor = Color(0xFFEDE8FA)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Round: 3/20",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EqubTextPrimary
                        )

                        Text(
                            text = "Next Payment: May 25, 2023",
                            fontSize = 13.sp,
                            color = EqubTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // My Position Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Position",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )
                    Text(
                        text = "7",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubPrimary,
                        modifier = Modifier.testTag("my_position_number")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Supporting Navigation Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp))
                        .clickable { onViewMembers() }
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = EqubPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Members (20)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp))
                        .clickable { onViewSchedule() }
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = EqubPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Schedule",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            EqubButton(
                text = "View Cycle",
                onClick = onViewCycle,
                testTag = "view_cycle_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
