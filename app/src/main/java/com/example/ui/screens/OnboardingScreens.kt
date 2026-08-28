package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.EqubButton
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }

    if (currentStep == 1) {
        OnboardingStep1(
            onNext = { currentStep = 2 },
            onSkip = onFinishOnboarding,
            modifier = modifier
        )
    } else {
        OnboardingStep2(
            onNext = onFinishOnboarding,
            onBack = { currentStep = 1 },
            modifier = modifier
        )
    }
}

@Composable
fun OnboardingStep1(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White,
        modifier = modifier.testTag("onboarding_step_1_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Brand header: Icon + "Equb"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(EqubPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Equb",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = EqubPrimary
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main heading
            Text(
                text = "Save Better\nTogether",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = EqubTextPrimary,
                lineHeight = 44.sp,
                modifier = Modifier.testTag("onboarding_1_title")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Illustration
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_onboarding_save),
                    contentDescription = "Save together illustration",
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator (1 of 3)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "1 of 3",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = EqubTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { 0.33f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EqubPrimary,
                    trackColor = EqubBorder
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action buttons: Skip & Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EqubButton(
                    text = "Skip",
                    onClick = onSkip,
                    isSecondary = true,
                    modifier = Modifier.weight(1f),
                    testTag = "onboarding_1_skip_button"
                )
                EqubButton(
                    text = "Next",
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    testTag = "onboarding_1_next_button"
                )
            }
        }
    }
}

@Composable
fun OnboardingStep2(
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White,
        modifier = modifier.testTag("onboarding_step_2_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand logo centered
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Equb",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EqubTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Illustration
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_onboarding_shield),
                    contentDescription = "Security and trust illustration",
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Transparent & Fair",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("onboarding_2_title")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Equb ensures your contributions and withdrawals are secure, fair, and fully transparent. Your financial future is protected.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = EqubTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Dots indicator (4 dots, 2nd active)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFDCD8E8)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF3B82F6)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFDCD8E8)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFDCD8E8)))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Buttons: Back & Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("onboarding_2_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = EqubTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Back",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary
                    )
                }

                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 140.dp)
                        .testTag("onboarding_2_next_button")
                ) {
                    Text(
                        text = "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
