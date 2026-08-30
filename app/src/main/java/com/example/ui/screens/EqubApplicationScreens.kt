package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.ui.components.equbTextFieldColors
import com.example.ui.theme.*

// =========================================================================
// STEP 1 OF 3: Personal Information
// =========================================================================
@Composable
fun EqubApplicationFormScreen(
    equb: EqubItem,
    onNext: (fullName: String, phone: String, reason: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("Abebe Bikila") }
    var phoneNumber by remember { mutableStateOf("911 234 567") }
    var savingsGoalReason by remember { mutableStateOf("") }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("apply_step1_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = EqubTextPrimary
                    )
                }
            }
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
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = {
                            onNext(
                                fullName.ifEmpty { "Abebe Bikila" },
                                phoneNumber.ifEmpty { "911 234 567" },
                                savingsGoalReason.ifEmpty { "Savings plan" }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("apply_step1_next_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EqubPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Next",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("apply_step1_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Progress & Step Title
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP 1 OF 3",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Personal Info",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = EqubTextSecondary
                        )
                    }

                    // Progress Bar (33% filled)
                    LinearProgressIndicator(
                        progress = { 0.33f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = EqubPrimary,
                        trackColor = Color(0xFFF7E6DC)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Apply in Equb",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary,
                        letterSpacing = (-0.4).sp
                    )

                    Text(
                        text = "Please provide your details to join this savings cycle.",
                        fontSize = 15.sp,
                        color = EqubTextSecondary,
                        lineHeight = 22.sp
                    )
                }
            }

            // Full Name Input
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Full Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text("Abebe Bikila", color = Color(0xFF7A7488)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = Color(0xFF7A7488),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = EqubTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = equbTextFieldColors(containerColor = Color.White),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_fullname_field")
                    )
                }
            }

            // Phone Number Input with Ethiopian Country Code
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Phone Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = { Text("911 234 567", color = Color(0xFF7A7488)) },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                            ) {
                                Text(
                                    text = "\uD83C\uDDEA\uD83C\uDDF9 +251",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EqubTextPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF7A7488),
                                    modifier = Modifier.size(18.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(1.dp)
                                        .background(Color(0xFFCBC3D9))
                                        .padding(horizontal = 2.dp)
                                )
                            }
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = EqubTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = equbTextFieldColors(containerColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_phone_field")
                    )
                }
            }

            // Reason Text Area
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Why do you want to join this Equb?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )

                    OutlinedTextField(
                        value = savingsGoalReason,
                        onValueChange = { savingsGoalReason = it },
                        placeholder = {
                            Text(
                                "Briefly describe your financial goal or reason for joining...",
                                color = Color(0xFF7A7488),
                                fontSize = 14.sp
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = EqubTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        minLines = 4,
                        maxLines = 6,
                        shape = RoundedCornerShape(16.dp),
                        colors = equbTextFieldColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_reason_field")
                    )
                }
            }
        }
    }
}

// =========================================================================
// STEP 2 OF 3: Identity Verification
// =========================================================================
@Composable
fun IdentityVerificationScreen(
    equb: EqubItem,
    fullName: String,
    phone: String,
    reason: String,
    onSubmitSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var finNumber by remember { mutableStateOf("") }
    var fanNumber by remember { mutableStateOf("") }
    var isImageUploaded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("identity_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = EqubPrimary
                    )
                }

                Text(
                    text = "EqubHub",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = EqubPrimary,
                    letterSpacing = (-0.4).sp
                )

                // Balanced placeholder
                Spacer(modifier = Modifier.size(48.dp))
            }
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
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = {
                            EqubRepository.submitApplication(
                                equbTitle = equb.title,
                                name = fullName,
                                phone = phone,
                                reason = reason
                            )
                            onSubmitSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("identity_submit_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EqubPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Submit Application",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        modifier = modifier.testTag("identity_verification_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Step 2 Pill Progress Indicator
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pill 1 (Done)
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(EqubPrimary)
                        )
                        // Pill 2 (Active/Done)
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(EqubPrimary)
                        )
                        // Pill 3 (Pending)
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEBD3C7))
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Identity Verification",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Step 2 of 3: Secure your account",
                            fontSize = 15.sp,
                            color = EqubTextSecondary
                        )
                    }
                }
            }

            // FIN Number
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "FIN Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )

                    OutlinedTextField(
                        value = finNumber,
                        onValueChange = { finNumber = it },
                        placeholder = { Text("Enter your FIN", color = Color(0xFF7A7488)) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = EqubTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = equbTextFieldColors(containerColor = Color.White),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("identity_fin_field")
                    )
                }
            }

            // FAN Number
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "FAN Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )

                    OutlinedTextField(
                        value = fanNumber,
                        onValueChange = { fanNumber = it },
                        placeholder = { Text("Enter your FAN", color = Color(0xFF7A7488)) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = EqubTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = equbTextFieldColors(containerColor = Color.White),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("identity_fan_field")
                    )
                }
            }

            // National ID Image Upload Area (Dashed Box)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "National ID Image",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )

                    val dashColor = if (isImageUploaded) EqubPrimary else Color(0xFFEBD3C7)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF9F6))
                            .drawBehind {
                                val stroke = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                )
                                drawRoundRect(
                                    color = dashColor,
                                    cornerRadius = CornerRadius(16.dp.toPx()),
                                    style = stroke
                                )
                            }
                            .clickable { isImageUploaded = !isImageUploaded }
                            .padding(16.dp)
                            .testTag("identity_upload_id_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isImageUploaded) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFE3B3)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Uploaded",
                                        tint = EqubPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = "National ID attached (id_photo.jpg)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EqubPrimary
                                )
                                Text(
                                    text = "Tap to replace photo",
                                    fontSize = 12.sp,
                                    color = EqubTextSecondary
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFE3B3)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PhotoCamera,
                                        contentDescription = "Camera",
                                        tint = EqubPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = "Tap to select from gallery\nor take photo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EqubPrimary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// STEP 3 OF 3: Application Submitted! (Complete)
// =========================================================================
@Composable
fun ApplicationSubmittedScreen(
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "iconFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim"
    )

    Scaffold(
        containerColor = EqubBackground,
        modifier = modifier.testTag("application_submitted_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFE2E8F8), RoundedCornerShape(24.dp))
                    .testTag("application_submitted_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Floating Animated Checkmark Circle
                    Box(
                        modifier = Modifier
                            .offset(y = floatOffset.dp)
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE3B3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = EqubPrimary,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "Application\nSubmitted!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.4).sp,
                        lineHeight = 32.sp,
                        modifier = Modifier.testTag("application_submitted_title")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Your application to join the Equb is being reviewed. We'll notify you once it's approved.",
                        fontSize = 15.sp,
                        color = EqubTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = onBackToDashboard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("application_back_to_dashboard_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EqubPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Back to Dashboard",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Step 3 of 3: Complete Progress Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(EqubPrimary.copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(EqubPrimary.copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(EqubPrimary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Step 3 of 3: Complete",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = EqubTextSecondary
            )
        }
    }
}

// =========================================================================
// My Applications List & Approval Views
// =========================================================================
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
                                listOf(EqubPrimary, EqubPrimaryLight)
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
                                text = "Weekly Car Fund",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "My Position: 4 / 15",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Total Members: 15",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Total Payout: 250,000 ETB",
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
