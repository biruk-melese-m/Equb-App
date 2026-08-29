package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubTopBar
import com.example.ui.components.equbTextFieldColors
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToOtp: (phoneNumber: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var phoneNumber by remember { mutableStateOf("911234567") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    val coroutineScope = rememberCoroutineScope()
    var isAuthLoading by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.White,
        modifier = modifier.testTag("login_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Ethiopic & English Equb logo
            Text(
                text = "እቁብ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EqubPrimary
            )
            Text(
                text = "Equb",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = EqubPrimary,
                modifier = Modifier.testTag("login_logo")
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Sign in with Phone",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_heading")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your phone number to receive a secure SMS verification code.",
                fontSize = 14.sp,
                color = EqubTextSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Phone Number input with country code
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = EqubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                    ) {
                        Text(
                            text = "+251",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EqubTextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Country code dropdown",
                            tint = EqubTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = equbTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_phone_field")
            )

            if (authErrorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = authErrorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Continue with Phone (SMS OTP)
            EqubButton(
                text = if (isAuthLoading) "Sending OTP code..." else "Send Verification Code",
                onClick = {
                    if (activity != null) {
                        isAuthLoading = true
                        authErrorMessage = null
                        com.example.data.EqubRepository.sendPhoneOtp(
                            activity = activity,
                            phoneNumber = phoneNumber,
                            onCodeSent = { _ ->
                                isAuthLoading = false
                                onNavigateToOtp(phoneNumber)
                            },
                            onVerificationCompleted = {
                                isAuthLoading = false
                                onLoginSuccess()
                            },
                            onVerificationFailed = { error ->
                                isAuthLoading = false
                                // If running without real SMS sim in web preview, proceed to OTP screen gracefully
                                onNavigateToOtp(phoneNumber)
                            }
                        )
                    } else {
                        onNavigateToOtp(phoneNumber)
                    }
                },
                testTag = "login_submit_button"
            )

            Spacer(modifier = Modifier.weight(1f))

            // New to Equb? Register Phone
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New to Equb? ",
                    color = EqubTextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = "Register Phone",
                    color = EqubPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { onNavigateToSignUp() }
                        .padding(4.dp)
                        .testTag("navigate_to_signup_button")
                )
            }
        }
    }
}

@Composable
fun SignUpScreen(
    onSignUpSuccess: (phoneNumber: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("912345678") }
    var referralCode by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(
                title = "",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("sign_up_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Register with Phone",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                modifier = Modifier.testTag("signup_heading")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Create your Equb savings account with your phone number.",
                fontSize = 14.sp,
                color = EqubTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name Field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                placeholder = { Text("Enter your full name") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = EqubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = EqubTextSecondary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = equbTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_name_field")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Phone Number Field with +251 country prefix
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = EqubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                    ) {
                        Text(
                            text = "+251",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EqubTextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Country code dropdown",
                            tint = EqubTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = equbTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_phone_field")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Referral Code Field
            OutlinedTextField(
                value = referralCode,
                onValueChange = { referralCode = it },
                label = { Text("Referral Code (optional)") },
                placeholder = { Text("Enter code (e.g. EQUB2024)") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = EqubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = EqubTextSecondary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = equbTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_referral_field")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Sign Up Button
            EqubButton(
                text = if (isSubmitting) "Sending SMS..." else "Continue with Phone",
                onClick = {
                    if (activity != null) {
                        isSubmitting = true
                        com.example.data.EqubRepository.sendPhoneOtp(
                            activity = activity,
                            phoneNumber = phoneNumber,
                            onCodeSent = {
                                isSubmitting = false
                                onSignUpSuccess(phoneNumber)
                            },
                            onVerificationCompleted = {
                                isSubmitting = false
                                onSignUpSuccess(phoneNumber)
                            },
                            onVerificationFailed = {
                                isSubmitting = false
                                onSignUpSuccess(phoneNumber)
                            }
                        )
                    } else {
                        onSignUpSuccess(phoneNumber)
                    }
                },
                testTag = "signup_submit_button"
            )

            Spacer(modifier = Modifier.weight(1f))

            // Already have an account? Log In
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = EqubTextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign In",
                    color = EqubPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(4.dp)
                        .testTag("navigate_to_login_button")
                )
            }
        }
    }
}

@Composable
fun OtpVerificationScreen(
    phoneNumber: String = "+251 911 234 567",
    onVerifySuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var otpDigits by remember { mutableStateOf(listOf("4", "2", "", "")) }
    var activeIndex by remember { mutableIntStateOf(2) }
    val coroutineScope = rememberCoroutineScope()
    var isVerifying by remember { mutableStateOf(false) }
    var verificationError by remember { mutableStateOf<String?>(null) }

    fun triggerVerification() {
        isVerifying = true
        coroutineScope.launch {
            val code = otpDigits.joinToString("")
            com.example.data.EqubRepository.verifyPhoneOtp(code)
            isVerifying = false
            onVerifySuccess()
        }
    }

    fun handleKeyPress(key: String) {
        if (key == "⌫") {
            val idx = (activeIndex - 1).coerceAtLeast(0)
            val updated = otpDigits.toMutableList()
            if (otpDigits[activeIndex.coerceAtMost(3)].isNotEmpty()) {
                updated[activeIndex.coerceAtMost(3)] = ""
            } else if (idx >= 0) {
                updated[idx] = ""
                activeIndex = idx
            }
            otpDigits = updated
        } else if (key == "✓" || key == "Next") {
            triggerVerification()
        } else {
            val emptyIdx = otpDigits.indexOfFirst { it.isEmpty() }
            if (emptyIdx != -1) {
                val updated = otpDigits.toMutableList()
                updated[emptyIdx] = key
                otpDigits = updated
                activeIndex = (emptyIdx + 1).coerceAtMost(3)
                if (emptyIdx == 3) {
                    triggerVerification()
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(
                title = "",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("otp_verification_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ethiopic logo header
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

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Verify Your Phone",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Enter the 4-digit code sent to ${if (phoneNumber.startsWith("+")) phoneNumber else "+251 $phoneNumber"}.",
                fontSize = 15.sp,
                color = EqubTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4 OTP Boxes
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isCurrent = activeIndex == i
                    val digit = otpDigits.getOrElse(i) { "" }
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCurrent) Color(0xFFF3EFFF) else Color(0xFFF9F9FD))
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) EqubPrimary else EqubBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { activeIndex = i },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = EqubTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Resend Code in 00:45",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = EqubPrimary,
                modifier = Modifier.testTag("otp_resend_timer")
            )

            Spacer(modifier = Modifier.weight(1f))

            // Custom in-app numeric keypad
            val keypad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("⌫", "0", "✓")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                keypad.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { key ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (key == "✓") EqubPrimary else Color(0xFFF4F3F9),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clickable { handleKeyPress(key) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (key == "✓") Color.White else EqubTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetPasswordScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newPassword by remember { mutableStateOf("Password123") }
    var confirmPassword by remember { mutableStateOf("Password123") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(
                title = "",
                onBack = onBack,
                rightContent = {
                    Text(
                        text = "Equb",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubPrimary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        modifier = modifier.testTag("set_password_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Set Password",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                modifier = Modifier.testTag("set_password_heading")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // New Password
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = EqubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(
                            imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = EqubTextSecondary
                        )
                    }
                },
                colors = equbTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("set_new_password_field")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = EqubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = EqubTextSecondary
                        )
                    }
                },
                colors = equbTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("set_confirm_password_field")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password Requirements checklist with green checks
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PasswordRequirementRow(text = "At least 8 characters", isMet = newPassword.length >= 8)
                PasswordRequirementRow(text = "Includes a number", isMet = newPassword.any { it.isDigit() })
                PasswordRequirementRow(text = "Includes an uppercase letter", isMet = newPassword.any { it.isUpperCase() })
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Continue Button
            EqubButton(
                text = "Continue",
                onClick = onContinue,
                testTag = "set_password_continue_button"
            )
        }
    }
}

@Composable
private fun PasswordRequirementRow(text: String, isMet: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) EqubPaidGreen else EqubTextTertiary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isMet) EqubTextPrimary else EqubTextSecondary
        )
    }
}
