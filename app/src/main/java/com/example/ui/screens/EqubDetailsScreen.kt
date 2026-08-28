package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
        containerColor = Color.White,
        topBar = {
            EqubTopBar(
                title = "Equb Details",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("equb_details_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Community Center Banner Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("equb_details_banner_image")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_community_center),
                    contentDescription = "Community Center",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Equb Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                modifier = Modifier.testTag("equb_details_heading")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 4 Info tiles in 2x2 grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                InfoTile(
                    title = "Total Amount",
                    value = equb.totalAmount,
                    icon = Icons.Default.MonetizationOn,
                    modifier = Modifier.weight(1f)
                )
                InfoTile(
                    title = "Members",
                    value = "${equb.maxMembers}",
                    icon = Icons.Default.Groups,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                InfoTile(
                    title = "Duration",
                    value = "${equb.durationMonths} Months",
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )
                InfoTile(
                    title = "Next Payment",
                    value = equb.nextPaymentDate,
                    icon = Icons.Default.AccessTime,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            EqubButton(
                text = "Apply Now",
                onClick = onApplyNow,
                testTag = "equb_apply_now_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoTile(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp)),
        color = Color(0xFFFBFBFE)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EqubPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = EqubTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary
            )
        }
    }
}
