package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun AboutEqubScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(
                title = "About Equb",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("about_equb_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo & Title
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(EqubPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Equb",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary
            )

            Text(
                text = "Version 4.0.1",
                fontSize = 14.sp,
                color = EqubTextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Purple mission statement card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = EqubPrimary)
            ) {
                Text(
                    text = "Equb is a digital platform for rotating savings and credit associations. We aim to digitize and empower communities to save, lend, and grow together. Our mission is to provide a secure and transparent way for members to manage their financial goals.",
                    fontSize = 15.sp,
                    color = Color.White,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Social media circles
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialIconCircle(icon = Icons.Default.Public)
                SocialIconCircle(icon = Icons.Default.Share)
                SocialIconCircle(icon = Icons.Default.AlternateEmail)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "www.equb.com",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = EqubPrimary,
                modifier = Modifier.testTag("about_equb_website_link")
            )
        }
    }
}

@Composable
fun SocialIconCircle(
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = CircleShape,
        color = Color(0xFFEDE8FA),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EqubPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
