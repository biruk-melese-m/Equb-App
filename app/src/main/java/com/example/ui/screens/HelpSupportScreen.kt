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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun HelpSupportScreen(
    onNavigateToRules: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showContactDialog by remember { mutableStateOf(false) }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Contact Support", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Our support team is available 24/7.")
                    Text("📞 Call: +251 911 000 000", fontWeight = FontWeight.SemiBold)
                    Text("✉️ Email: support@equb.com", fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Close", color = EqubPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Help and Support",
                onBack = onBack,
                rightIcon = Icons.Outlined.Search,
                onRightAction = { /* search */ }
            )
        },
        modifier = modifier.testTag("help_support_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("How can we help you?") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EqubTextSecondary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = EqubPrimary,
                    unfocusedBorderColor = EqubCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("help_search_field")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Topic Cards
            HelpTopicCard(
                icon = Icons.Outlined.CreditCard,
                title = "Payments",
                description = "How to make and track payments",
                onClick = { /* FAQ on payments */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            HelpTopicCard(
                icon = Icons.Outlined.Person,
                title = "Account",
                description = "Manage your profile and settings",
                onClick = { /* FAQ on accounts */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            HelpTopicCard(
                icon = Icons.Outlined.Description,
                title = "Equb Rules",
                description = "Learn about Equb regulations",
                onClick = onNavigateToRules
            )

            Spacer(modifier = Modifier.weight(1f))

            EqubButton(
                text = "Contact Support",
                onClick = { showContactDialog = true },
                testTag = "contact_support_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HelpTopicCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEDE8FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = EqubPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = EqubTextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = EqubTextTertiary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
