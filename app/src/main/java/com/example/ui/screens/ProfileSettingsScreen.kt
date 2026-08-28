package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EqubRepository
import com.example.ui.components.EqubAvatar
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun ProfileSettingsScreen(
    onNavigateToRules: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToInvite: () -> Unit,
    onLogout: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val profile by EqubRepository.userProfile.collectAsState()

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Profile and Settings",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("profile_and_settings_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // User Profile Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, EqubCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        EqubAvatar(
                            name = profile.fullName,
                            modifier = Modifier.size(56.dp)
                        )
                        Column {
                            Text(
                                text = profile.fullName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EqubTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = profile.phone,
                                fontSize = 14.sp,
                                color = EqubTextSecondary
                            )
                        }
                    }
                }
            }

            // Settings Menu Group 1
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, EqubCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsMenuItem(
                            icon = Icons.Outlined.Person,
                            title = "Edit Profile",
                            onClick = { /* edit profile dialog */ }
                        )
                        HorizontalDivider(color = EqubDivider, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Outlined.Lock,
                            title = "Change Password",
                            onClick = { /* change password */ }
                        )
                        HorizontalDivider(color = EqubDivider, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Outlined.CreditCard,
                            title = "Payment Methods",
                            onClick = { /* payment methods */ }
                        )
                        HorizontalDivider(color = EqubDivider, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Outlined.Notifications,
                            title = "Notification Settings",
                            onClick = { /* notification settings */ }
                        )
                        HorizontalDivider(color = EqubDivider, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Outlined.Description,
                            title = "Terms & Conditions",
                            onClick = onNavigateToRules
                        )
                    }
                }
            }

            // Settings Menu Group 2: Help, Invite, About
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, EqubCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsMenuItem(
                            icon = Icons.Outlined.HelpOutline,
                            title = "Help & Support",
                            onClick = onNavigateToHelp
                        )
                        HorizontalDivider(color = EqubDivider, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Outlined.Share,
                            title = "Invite & Share Referral",
                            onClick = onNavigateToInvite
                        )
                        HorizontalDivider(color = EqubDivider, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Outlined.Info,
                            title = "About Equb",
                            onClick = onNavigateToAbout
                        )
                    }
                }
            }

            // Logout Button
            item {
                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = EqubRejectedRed
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(EqubRejectedRed)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("logout_button")
                ) {
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubRejectedRed
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EqubPrimary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = EqubTextPrimary
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = EqubTextTertiary,
            modifier = Modifier.size(14.dp)
        )
    }
}
