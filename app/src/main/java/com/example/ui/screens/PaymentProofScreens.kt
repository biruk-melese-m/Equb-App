package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
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
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedMethod by remember { mutableStateOf("Telebirr") }
    var transactionId by remember { mutableStateOf("") }
    var fileUploaded by remember { mutableStateOf(false) }

    val paymentMethods = listOf("Telebirr", "CBE (Commercial Bank)", "Awash Bank", "Abyssinia Bank")

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Submit Payment Slip",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("submit_payment_proof_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Official Bank Transfer Instructions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, EqubCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = EqubPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Official Deposit Accounts",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Transfer your round contribution (5,000 ETB) to any of the verified accounts below, then copy your Transaction ID and upload the slip.",
                        fontSize = 12.sp,
                        color = EqubTextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Account rows
                    DepositAccountRow(
                        bankName = "Telebirr Merchant",
                        accountNumber = "0911234567",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString("0911234567"))
                            Toast.makeText(context, "Telebirr number copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DepositAccountRow(
                        bankName = "CBE (Commercial Bank)",
                        accountNumber = "1000123456789",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString("1000123456789"))
                            Toast.makeText(context, "CBE Account number copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DepositAccountRow(
                        bankName = "Awash Bank",
                        accountNumber = "01320492819200",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString("01320492819200"))
                            Toast.makeText(context, "Awash Account number copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Method Selector
            Text(
                text = "Select Payment Method Used",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paymentMethods.take(2).forEach { method ->
                    val isSelected = selectedMethod == method
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedMethod = method },
                        color = if (isSelected) EqubPrimaryContainer else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) EqubPrimary else EqubCardBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = method,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) EqubPrimary else EqubTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paymentMethods.drop(2).forEach { method ->
                    val isSelected = selectedMethod == method
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedMethod = method },
                        color = if (isSelected) EqubPrimaryContainer else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) EqubPrimary else EqubCardBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = method,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) EqubPrimary else EqubTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Transaction ID Field
            Text(
                text = "Bank Transaction / Reference ID",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = transactionId,
                onValueChange = { transactionId = it },
                placeholder = { Text("e.g. FT24083091823 or TB-982312") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = EqubTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                shape = RoundedCornerShape(12.dp),
                colors = equbTextFieldColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_id_field")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Upload Receipt Card
            Text(
                text = "Deposit Slip / Transfer Screenshot",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.5.dp,
                        color = if (fileUploaded) EqubPaidGreen else EqubPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { fileUploaded = !fileUploaded }
                    .testTag("upload_receipt_box"),
                color = if (fileUploaded) Color(0xFFF0FDF4) else Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (fileUploaded) Color(0xFFDCFCE7) else EqubPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (fileUploaded) Icons.Default.CheckCircle else Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = if (fileUploaded) EqubPaidGreen else EqubPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (fileUploaded) "deposit_slip_${selectedMethod.lowercase().replace(" ", "_")}.jpg Attached" else "Attach Receipt Photo or Screenshot",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (fileUploaded) "Tap to change image" else "Tap to choose from Gallery or Camera (Max 5MB)",
                        fontSize = 12.sp,
                        color = EqubTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            EqubButton(
                text = "Submit for Admin Verification",
                onClick = {
                    EqubRepository.submitPaymentProof(
                        transactionId = transactionId.ifBlank { "TX-99823145" }
                    )
                    onSubmitSuccess()
                },
                testTag = "submit_payment_proof_button"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DepositAccountRow(
    bankName: String,
    accountNumber: String,
    onCopy: () -> Unit
) {
    Surface(
        color = EqubBackground,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = bankName, fontSize = 11.sp, color = EqubTextSecondary)
                Text(text = accountNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EqubTextPrimary)
            }
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "Copy Account Number",
                    tint = EqubPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
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

            // Clock with gears icon in light warm circle
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(EqubPrimaryContainer),
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
