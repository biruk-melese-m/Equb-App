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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EqubRepository
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubTopBar
import com.example.ui.components.equbTextFieldColors
import com.example.ui.theme.*

@Composable
fun SubmitPaymentProofScreen(
    onSubmitSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var transactionId by remember { mutableStateOf("") }
    var fileUploaded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(
                title = "Submit Payment Proof",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("submit_payment_proof_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Transaction ID Field
            OutlinedTextField(
                value = transactionId,
                onValueChange = { transactionId = it },
                label = { Text("Transaction ID") },
                placeholder = { Text("Enter Transaction ID") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = EqubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                shape = RoundedCornerShape(12.dp),
                colors = equbTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_id_field")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Upload Receipt Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.5.dp,
                        color = if (fileUploaded) EqubPaidGreen else EqubPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { fileUploaded = !fileUploaded }
                    .testTag("upload_receipt_box"),
                color = if (fileUploaded) Color(0xFFF0FDF4) else Color(0xFFF9F7FE)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (fileUploaded) Color(0xFFDCFCE7) else Color(0xFFEDE8FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (fileUploaded) Icons.Default.CheckCircle else Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = if (fileUploaded) EqubPaidGreen else EqubPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (fileUploaded) "receipt_telebirr_5000.jpg Attached" else "Upload Receipt",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (fileUploaded) "Tap to change file" else "JPG, PNG, or PDF (Max 5MB)",
                        fontSize = 13.sp,
                        color = EqubTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tap to select from gallery or take photo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EqubPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            EqubButton(
                text = "Submit",
                onClick = {
                    EqubRepository.submitPaymentProof(
                        transactionId = transactionId.ifBlank { "TX-99823145" }
                    )
                    onSubmitSuccess()
                },
                testTag = "submit_payment_proof_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PaymentPendingReviewScreen(
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White,
        modifier = modifier.testTag("payment_pending_review_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "እቁብ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EqubPrimary
            )
            Text(
                text = "Equb",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = EqubPrimary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Clock with gears icon in light purple circle
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE8FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = EqubPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Payment Under Review",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("payment_pending_title")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your payment proof is being verified by the admin staff.",
                fontSize = 15.sp,
                color = EqubTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(44.dp))

            EqubButton(
                text = "Back to Dashboard",
                onClick = onBackToDashboard,
                isOutlined = true,
                testTag = "pending_back_to_dashboard_button"
            )
        }
    }
}

@Composable
fun PaymentApprovedSuccessScreen(
    onViewPaymentHistory: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(title = "", onBack = onBack)
        },
        modifier = modifier.testTag("payment_approved_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                text = "Payment Approved!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("payment_approved_title")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Round 3 payment of 5,000 ETB has been confirmed.",
                fontSize = 16.sp,
                color = EqubTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(44.dp))

            EqubButton(
                text = "View Payment History",
                onClick = onViewPaymentHistory,
                testTag = "view_payment_history_button"
            )
        }
    }
}
