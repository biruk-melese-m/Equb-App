package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Member
import com.example.ui.components.EqubAvatar
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun MemberProfileDetailScreen(
    member: Member,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = EqubBackground,
        modifier = modifier.testTag("member_profile_detail_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Purple Curved Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(EqubPrimary)
            ) {
                EqubTopBar(
                    title = "Member Profile",
                    onBack = onBack,
                    contentColor = Color.White
                )
            }

            // Overlapping Circular Avatar
            Box(
                modifier = Modifier
                    .offset(y = (-45).dp)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
            ) {
                EqubAvatar(
                    name = member.name,
                    bgColor = Color(member.initialColor),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Member Name & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-35).dp)
            ) {
                Text(
                    text = member.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EqubTextPrimary,
                    modifier = Modifier.testTag("member_profile_name")
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Member",
                    fontSize = 14.sp,
                    color = EqubTextSecondary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-20).dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Two Stat Cards: Total Contributions | Rotation Position
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MemberStatCard(
                        title = "Total Contributions",
                        value = member.totalContributions,
                        icon = Icons.Default.AccountBalanceWallet,
                        modifier = Modifier.weight(1f)
                    )
                    MemberStatCard(
                        title = "Rotation Position",
                        value = member.rotationPosition,
                        icon = Icons.Default.EmojiEvents,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Contact Information Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, EqubCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Contact Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ContactRow(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = member.phone
                        )

                        HorizontalDivider(
                            color = EqubDivider,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        ContactRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = member.email
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemberStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EqubPrimary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = EqubTextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary
            )
        }
    }
}

@Composable
fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EqubPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = EqubTextSecondary
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = EqubTextPrimary
            )
        }
    }
}
