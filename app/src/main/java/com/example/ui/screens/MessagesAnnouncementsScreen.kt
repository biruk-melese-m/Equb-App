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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Announcement
import com.example.data.EqubRepository
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun MessagesAnnouncementsScreen(
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val announcements by EqubRepository.announcements.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Payment Due", "Draw Winner", "Security Alert", "General")

    val filteredAnnouncements = remember(announcements, selectedCategory) {
        if (selectedCategory == "All") announcements
        else announcements.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Announcements",
                onBack = onBack,
                rightIcon = Icons.Outlined.NotificationsActive,
                onRightAction = { /* notifications */ }
            )
        },
        modifier = modifier.testTag("announcements_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Official Broadcast Notice Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                color = EqubPrimary.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, EqubPrimary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EqubPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Official Equb Bulletins",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubPrimary
                        )
                        Text(
                            text = "Verified notices, round draw results, and payment deadlines from admins.",
                            fontSize = 12.sp,
                            color = EqubTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = category },
                        color = if (isSelected) EqubPrimary else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) EqubPrimary else EqubCardBorder
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else EqubTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Announcements Stream
            if (filteredAnnouncements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Campaign,
                            contentDescription = null,
                            tint = EqubTextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No announcements in this category",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = EqubTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAnnouncements) { ann ->
                        AnnouncementItemCard(announcement = ann)
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementItemCard(
    announcement: Announcement,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (announcement.isUrgent) Color(0xFFEF4444) else EqubCardBorder,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (announcement.isUrgent) Color(0xFFFEF2F2) else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (announcement.isUrgent) Color(0xFFFEE2E2) else Color(0xFFEDE8FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (announcement.isUrgent) Icons.Default.WarningAmber else Icons.Default.Campaign,
                            contentDescription = null,
                            tint = if (announcement.isUrgent) Color(0xFFDC2626) else EqubPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = announcement.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary
                        )
                        Text(
                            text = announcement.date,
                            fontSize = 11.sp,
                            color = EqubTextSecondary
                        )
                    }
                }

                Surface(
                    color = if (announcement.isUrgent) Color(0xFFDC2626) else EqubPrimaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (announcement.isUrgent) "URGENT" else announcement.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (announcement.isUrgent) Color.White else EqubPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = announcement.description,
                fontSize = 13.sp,
                color = EqubTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
