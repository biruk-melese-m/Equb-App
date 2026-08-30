package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.EqubItem
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun EqubDetailsScreen(
    equb: EqubItem,
    onApplyNow: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Equb Details",
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    EqubButton(
                        text = if (equb.isUserJoined) "Already Joined • View Status" else "Apply to Join Equb",
                        onClick = onApplyNow,
                        testTag = "equb_apply_now_button"
                    )
                }
            }
        },
        modifier = modifier.testTag("equb_details_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // 1. HERO BANNER & EQUB IDENTITY
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Hero Image with category overlay tag
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .testTag("equb_details_banner_image")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_community_center),
                                contentDescription = "Community Center",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.25f))
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.White.copy(alpha = 0.95f),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "CATEGORY: ${equb.category.uppercase()}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EqubPrimary,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Title and Subtitle
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = equb.title,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = EqubTextPrimary,
                                modifier = Modifier.testTag("equb_details_heading")
                            )
                            Text(
                                text = "Rotating savings association with verified participant pool and escrow guarantee.",
                                fontSize = 12.sp,
                                color = EqubTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 2. OCD SPECIFICATION LEDGER TABLE
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC))
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "METRIC / SPECIFICATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EqubTextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "RECORDED VALUE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EqubTextSecondary,
                                letterSpacing = 0.5.sp
                            )
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                        // Row 1: Total Pool Value
                        DetailsTableRow(
                            label = "Total Pool Value",
                            sublabel = "Full rotation gross payout",
                            value = equb.totalAmount,
                            isPrimary = true
                        )

                        // Row 2: Contribution per cycle
                        DetailsTableRow(
                            label = "Cycle Contribution",
                            sublabel = "Fixed per-member monthly due",
                            value = equb.monthlyContribution
                        )

                        // Row 3: Rotation Term
                        DetailsTableRow(
                            label = "Rotation Term",
                            sublabel = "${equb.totalRounds} scheduled rounds",
                            value = "${equb.durationMonths} Months"
                        )

                        // Row 4: Capacity
                        DetailsTableRow(
                            label = "Member Capacity",
                            sublabel = if (equb.currentMembers >= equb.maxMembers) "Pool fully registered" else "${equb.maxMembers - equb.currentMembers} available seats",
                            value = "${equb.currentMembers} / ${equb.maxMembers} Members",
                            badge = if (equb.currentMembers >= equb.maxMembers) "FULL" else "OPEN"
                        )

                        // Row 5: Next Deadline
                        DetailsTableRow(
                            label = "Next Payment Deadline",
                            sublabel = "Cycle due date",
                            value = equb.nextPaymentDate,
                            badge = "${equb.daysLeftTillDue}D LEFT",
                            isDueBadge = true
                        )

                        // Row 6: Cycle Frequency
                        DetailsTableRow(
                            label = "Frequency",
                            sublabel = "Payment and draw interval",
                            value = "Monthly (Every 30 Days)",
                            isLast = true
                        )
                    }
                }
            }

            // 3. VERIFICATION & ESCROW POLICY
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security Protocol",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "ESCROW & SECURITY POLICY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = equb.securityDetail,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsTableRow(
    label: String,
    sublabel: String,
    value: String,
    badge: String? = null,
    isPrimary: Boolean = false,
    isDueBadge: Boolean = false,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = EqubTextPrimary
                )
                Text(
                    text = sublabel,
                    fontSize = 11.sp,
                    color = EqubTextSecondary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isDueBadge) Color(0xFFFFF7ED) else if (badge == "FULL") Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDueBadge) Color(0xFFFFEDD5) else if (badge == "FULL") Color(0xFFFECACA) else Color(0xFFA7F3D0)
                        )
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDueBadge) Color(0xFFC2410C) else if (badge == "FULL") Color(0xFFDC2626) else Color(0xFF059669),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrimary) EqubPrimary else EqubTextPrimary
                )
            }
        }

        if (!isLast) {
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }
    }
}

