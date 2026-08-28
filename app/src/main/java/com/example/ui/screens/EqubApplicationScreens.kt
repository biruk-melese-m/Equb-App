package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.EqubApplication
import com.example.data.EqubItem
import com.example.data.EqubRepository
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubStatusBadge
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun EqubApplicationFormScreen(
    equb: EqubItem,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("Abebe Bikila") }
    var phoneNumber by remember { mutableStateOf("911234567") }
    var savingsGoalReason by remember { mutableStateOf("I want to save towards opening my retail shop inventory and building systematic savings habits.") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(
                title = "Apply in Equb",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("apply_in_equb_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Step 1 of 3 Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step 1 of 3",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = EqubTextSecondary
                )
                Text(
                    text = equb.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EqubPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { 0.33f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EqubPrimary,
                trackColor = Color(0xFFEBEBF5)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Personal Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Full Name Field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                placeholder = { Text("e.g., Abebe Bikila") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EqubPrimary,
                    unfocusedBorderColor = EqubBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_fullname_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                placeholder = { Text("e.g., 911234567") },
                leadingIcon = {
                    Text(
                        text = "+251 | ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextSecondary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EqubPrimary,
                    unfocusedBorderColor = EqubBorder
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_phone_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Why do you want to join this Equb?
            OutlinedTextField(
                value = savingsGoalReason,
                onValueChange = { savingsGoalReason = it },
                label = { Text("Why do you want to join this Equb?") },
                placeholder = { Text("Explain your savings goals...") },
                minLines = 4,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EqubPrimary,
                    unfocusedBorderColor = EqubBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_reason_field")
            )

            Spacer(modifier = Modifier.weight(1f))

            EqubButton(
                text = "Next",
                onClick = {
                    EqubRepository.submitApplication(
                        equbTitle = equb.title,
                        name = fullName,
                        phone = phoneNumber,
                        reason = savingsGoalReason
                    )
                    onSubmit()
                },
                testTag = "apply_form_submit_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ApplicationSubmittedScreen(
    onViewMyApplications: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White,
        modifier = modifier.testTag("application_submitted_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Big green checkmark circle
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Application Submitted!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("application_submitted_title")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Your application is under review. You will be notified once it is approved.",
                fontSize = 16.sp,
                color = EqubTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(44.dp))

            EqubButton(
                text = "View My Applications",
                onClick = onViewMyApplications,
                isOutlined = true,
                testTag = "view_my_applications_button"
            )
        }
    }
}

@Composable
fun MyApplicationsScreen(
    onSelectApplication: (EqubApplication) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val applications by EqubRepository.applications.collectAsState()
    var selectedTab by remember { mutableStateOf("Pending") }

    val filtered = applications.filter { it.status.equals(selectedTab, ignoreCase = true) }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "My Applications",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("my_applications_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Segmented Tabs: Pending | Approved | Rejected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBEBF5))
                    .padding(4.dp)
            ) {
                listOf("Pending", "Approved", "Rejected").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) EqubTextPrimary else EqubTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No $selectedTab applications found.",
                        color = EqubTextSecondary,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filtered) { app ->
                        ApplicationCard(
                            application = app,
                            onClick = { onSelectApplication(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(
    application: EqubApplication,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("application_card_${application.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = application.equbTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EqubTextPrimary
                )
                EqubStatusBadge(status = application.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = application.amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EqubPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Applied on ${application.appliedDate}",
                fontSize = 13.sp,
                color = EqubTextSecondary
            )
        }
    }
}

@Composable
fun ApplicationApprovedScreen(
    onGoToMyEqub: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(title = "", onBack = onBack)
        },
        modifier = modifier.testTag("application_approved_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Confetti illustration
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_celebrate_approval),
                    contentDescription = "Celebration",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Application Approved",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("application_approved_title")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Congratulations! Your application has been approved. You are now a member of this Equb.",
                fontSize = 15.sp,
                color = EqubTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            EqubButton(
                text = "Go to My Equb",
                onClick = onGoToMyEqub,
                testTag = "go_to_my_equb_button"
            )
        }
    }
}

@Composable
fun EqubJoinedScreen(
    onGoToMyEqub: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(title = "Equb Overview", onBack = onBack)
        },
        modifier = modifier.testTag("equb_joined_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Blue/Purple Gradient Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF4C30A0), Color(0xFF381F78))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Monthly Savings Equb",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "My Position: 7 / 20",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Total Members: 20",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Total Payout: 100,550 ETB",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            EqubButton(
                text = "Go to My Equb",
                onClick = onGoToMyEqub,
                testTag = "joined_overview_go_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
