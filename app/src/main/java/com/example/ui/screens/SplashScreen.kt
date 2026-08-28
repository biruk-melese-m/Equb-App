package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EqubPrimary
import com.example.ui.theme.EqubTextPrimary
import com.example.ui.theme.EqubTextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { onSplashFinished() }
            .testTag("splash_screen")
    ) {
        // Decorative soft purple waves at the bottom
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val path1 = Path().apply {
                moveTo(0f, height * 0.85f)
                cubicTo(
                    width * 0.3f, height * 0.80f,
                    width * 0.6f, height * 0.90f,
                    width, height * 0.78f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path1, color = Color(0xFFF3EFFF))

            val path2 = Path().apply {
                moveTo(0f, height * 0.90f)
                cubicTo(
                    width * 0.4f, height * 0.86f,
                    width * 0.7f, height * 0.95f,
                    width, height * 0.88f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path2, color = Color(0xFFEAE2FC))
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ethiopic title
            Text(
                text = "እቁብ",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = EqubPrimary,
                modifier = Modifier.testTag("splash_ethiopic_logo")
            )
            Spacer(modifier = Modifier.height(4.dp))
            // English title
            Text(
                text = "Equb",
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
                color = EqubPrimary,
                letterSpacing = (-1).sp,
                modifier = Modifier.testTag("splash_english_logo")
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Save Together.\nGrow Together.",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = EqubTextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier.testTag("splash_tagline")
            )
        }
    }
}
