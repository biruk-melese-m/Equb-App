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
import com.example.data.Announcement
import com.example.data.ChatMessage
import com.example.data.EqubRepository
import com.example.ui.components.EqubAvatar
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun MessagesAnnouncementsScreen(
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val messages by EqubRepository.messages.collectAsState()
    val announcements by EqubRepository.announcements.collectAsState()
    var selectedTab by remember { mutableStateOf("Messages") }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Messages / Announcements",
                onBack = onBack,
                rightIcon = Icons.Outlined.Search,
                onRightAction = { /* search */ }
            )
        },
        modifier = modifier.testTag("messages_announcements_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Segmented Tab Bar
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Messages", "Announcements").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) EqubPrimary else EqubTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(if (isSelected) EqubPrimary else Color.Transparent)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == "Messages") {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages) { msg ->
                        MessageItemCard(message = msg)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(announcements) { ann ->
                        AnnouncementItemCard(announcement = ann)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItemCard(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                EqubAvatar(
                    name = message.senderName,
                    bgColor = if (message.senderName.contains("Admin")) EqubPrimary else Color(0xFFE91E63)
                )

                Column {
                    Text(
                        text = message.senderName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = EqubTextSecondary,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = message.time,
                fontSize = 12.sp,
                color = EqubTextTertiary
            )
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
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE8FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = EqubPrimary,
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
