package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EqubPrimary
import com.example.ui.theme.EqubSecondary
import com.example.ui.theme.EqubTextSecondary

data class OnboardingPageData(
    val id: Int,
    val title: String,
    val description: String,
    val imageRes: Int,
    val buttonText: String,
    val buttonColor: Color,
    val activeDotColor: Color
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPageData(
            id = 0,
            title = "Welcome to the\nFuture of Finance!",
            description = "Discover a smarter way to save, contribute, and achieve your financial goals together.",
            imageRes = R.drawable.img_onboarding_bank_1788098767458,
            buttonText = "Get Started",
            buttonColor = Color(0xFFCA2851),
            activeDotColor = Color(0xFFCA2851)
        ),
        OnboardingPageData(
            id = 1,
            title = "Fast, Secure\nTransactions",
            description = "Transfer funds globally with minimal fees and maximum security powered by blockchain technology.",
            imageRes = R.drawable.img_onboarding_security_1788098780012,
            buttonText = "Next",
            buttonColor = Color(0xFFFF6766),
            activeDotColor = Color(0xFFCA2851)
        ),
        OnboardingPageData(
            id = 2,
            title = "Your Investments,\nSimplified",
            description = "Track, manage, and grow your savings with clear insights and powerful analytics in one place.",
            imageRes = R.drawable.img_onboarding_analytics_1788098793173,
            buttonText = "Start Now",
            buttonColor = Color(0xFFFFA043),
            activeDotColor = Color(0xFFCA2851)
        )
    )

    val page = pages[currentPage]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCFCFC))
            .pointerInput(currentPage) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -30 && currentPage < 2) {
                        currentPage++
                    } else if (dragAmount > 30 && currentPage > 0) {
                        currentPage--
                    }
                }
            }
            .testTag("onboarding_screen")
    ) {
        // Decorative warm layered wave background at the bottom
        OnboardingBottomWaves(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
        )

        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header: Skip Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (currentPage < 2) {
                    Text(
                        text = "Skip",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextSecondary,
                        modifier = Modifier
                            .clickable { onFinishOnboarding() }
                            .padding(8.dp)
                            .testTag("onboarding_skip_button")
                    )
                } else {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }

            // Central Animated Content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "onboarding_page_transition"
            ) { targetIndex ->
                val targetPage = pages[targetIndex]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 3D Illustration
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = targetPage.imageRes),
                            contentDescription = targetPage.title,
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = targetPage.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCA2851),
                        textAlign = TextAlign.Center,
                        lineHeight = 35.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_${targetIndex + 1}_title")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Subtitle / Description
                    Text(
                        text = targetPage.description,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF6B5F64),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .testTag("onboarding_${targetIndex + 1}_description")
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 3-Dot Pagination Indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("onboarding_dots_indicator")
                    ) {
                        repeat(3) { dotIndex ->
                            val isActive = dotIndex == targetIndex
                            Box(
                                modifier = Modifier
                                    .size(if (isActive) 10.dp else 9.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isActive) targetPage.activeDotColor
                                        else Color(0xFFF7D5DC)
                                    )
                                    .clickable {
                                        currentPage = dotIndex
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Bottom CTA Button
            Button(
                onClick = {
                    if (currentPage < 2) {
                        currentPage++
                    } else {
                        onFinishOnboarding()
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = page.buttonColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("onboarding_action_button")
            ) {
                Text(
                    text = page.buttonText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

/**
 * Custom Canvas drawing the smooth organic layered bottom waves
 * in soft yellow-peach, vibrant orange, and coral/red.
 */
@Composable
fun OnboardingBottomWaves(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Top soft creamy peach wave layer
        val path1 = Path().apply {
            moveTo(0f, height * 0.45f)
            cubicTo(
                width * 0.25f, height * 0.15f,
                width * 0.70f, height * 0.75f,
                width, height * 0.35f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path1,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFE8D1).copy(alpha = 0.75f),
                    Color(0xFFFFD8BC).copy(alpha = 0.85f)
                ),
                startY = height * 0.2f,
                endY = height
            )
        )

        // Middle warm orange wave layer
        val path2 = Path().apply {
            moveTo(0f, height * 0.65f)
            cubicTo(
                width * 0.35f, height * 0.40f,
                width * 0.65f, height * 0.85f,
                width, height * 0.55f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path2,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFFB173).copy(alpha = 0.75f),
                    Color(0xFFFFA55E).copy(alpha = 0.60f)
                )
            )
        )

        // Bottom vibrant coral/crimson wave accent in bottom-left
        val path3 = Path().apply {
            moveTo(0f, height * 0.78f)
            cubicTo(
                width * 0.20f, height * 0.70f,
                width * 0.45f, height * 0.95f,
                width * 0.65f, height
            )
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path3,
            color = Color(0xFFFF6766).copy(alpha = 0.70f)
        )

        // Bottom vibrant red accent on the very corner
        val path4 = Path().apply {
            moveTo(0f, height * 0.88f)
            cubicTo(
                width * 0.15f, height * 0.84f,
                width * 0.30f, height * 0.96f,
                width * 0.40f, height
            )
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path4,
            color = Color(0xFFCA2851).copy(alpha = 0.85f)
        )
    }
}
