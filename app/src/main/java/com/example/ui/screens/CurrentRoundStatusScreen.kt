package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.Member
import com.example.ui.components.EqubAvatar
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubStatusBadge
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun CurrentRoundStatusScreen(
    onNavigateToMembers: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val members by EqubRepository.members.collectAsState()
    val pendingMembers = members.filter { it.isPending }
    var remindedSet by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Current Round Status",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("current_round_status_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Main Round Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, EqubCardBorder, RoundedCornerShape(16.dp))
                    .testTag("current_round_status_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Round 3 of 20",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary
                        )
                        Surface(
                            color = Color(0xFFEDE8FA),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Current Active Cycle",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EqubPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "18/20 Members Paid",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { 0.90f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EqubPrimary,
                        trackColor = Color(0xFFEDE8FA)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3 Stat Boxes: Total Members | Paid | Pending
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatPill(title = "Total Members", value = "20", modifier = Modifier.weight(1f))
                        StatPill(title = "Paid", value = "18", valueColor = EqubPaidGreen, modifier = Modifier.weight(1f))
                        StatPill(title = "Pending", value = "2", valueColor = EqubPendingYellow, modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Subheading: Pending Members (2)
            Text(
                text = "Pending Members (${pendingMembers.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(pendingMembers) { member ->
                    val isReminded = remindedSet.contains(member.id)
                    PendingMemberRow(
                        member = member,
                        isReminded = isReminded,
                        onRemind = {
                            remindedSet = remindedSet + member.id
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EqubButton(
                    text = "Members List",
                    onClick = onNavigateToMembers,
                    isSecondary = true,
                    modifier = Modifier.weight(1f),
                    testTag = "members_list_nav_button"
                )
                EqubButton(
                    text = "Full Schedule",
                    onClick = onNavigateToSchedule,
                    modifier = Modifier.weight(1f),
                    testTag = "schedule_nav_button"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PendingMemberRow(
    member: Member,
    isReminded: Boolean,
    onRemind: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EqubAvatar(
                    name = member.name,
                    bgColor = Color(member.initialColor),
                    modifier = Modifier.size(40.dp)
                )
                Column {
                    Text(
                        text = member.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )
                    Text(
                        text = "Position: ${member.rotationPosition}",
                        fontSize = 12.sp,
                        color = EqubTextSecondary
                    )
                }
            }

            Button(
                onClick = onRemind,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isReminded) Color(0xFFEDE8FA) else EqubPrimary,
                    contentColor = if (isReminded) EqubPrimary else Color.White
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("remind_button_${member.id}")
            ) {
                Text(
                    text = if (isReminded) "Sent" else "Remind",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StatPill(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = EqubTextPrimary
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp)),
        color = Color(0xFFF7F6FC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = EqubTextSecondary
            )
        }
    }
}
