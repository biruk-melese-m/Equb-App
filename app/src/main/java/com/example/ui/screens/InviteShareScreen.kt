package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.*

@Composable
fun InviteShareScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by EqubRepository.userProfile.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Invite & Share",
                onBack = onBack,
                rightIcon = Icons.Outlined.Share,
                onRightAction = {
                    Toast.makeText(context, "Link shared!", Toast.LENGTH_SHORT).show()
                }
            )
        },
        modifier = modifier.testTag("invite_share_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main White Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, EqubCardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE8FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = EqubPrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Invite friends and earn rewards.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Unique referral code box with Copy button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, EqubBorder, RoundedCornerShape(12.dp)),
                        color = Color(0xFFF9F7FE)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Unique referral code",
                                    fontSize = 11.sp,
                                    color = EqubTextSecondary
                                )
                                Text(
                                    text = profile.referralCode,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EqubPrimary
                                )
                            }

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(profile.referralCode))
                                    Toast.makeText(context, "Code copied: ${profile.referralCode}", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EqubPrimary),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("copy_referral_code_button")
                            ) {
                                Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Share circles: WhatsApp, Telegram, Messenger, Share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShareOptionCircle(name = "WhatsApp", color = Color(0xFF25D366), icon = Icons.Default.Chat) {
                            Toast.makeText(context, "Sharing to WhatsApp...", Toast.LENGTH_SHORT).show()
                        }
                        ShareOptionCircle(name = "Telegram", color = Color(0xFF0088CC), icon = Icons.Default.Send) {
                            Toast.makeText(context, "Sharing to Telegram...", Toast.LENGTH_SHORT).show()
                        }
                        ShareOptionCircle(name = "Messenger", color = Color(0xFF0084FF), icon = Icons.Default.Forum) {
                            Toast.makeText(context, "Sharing to Messenger...", Toast.LENGTH_SHORT).show()
                        }
                        ShareOptionCircle(name = "Share", color = EqubPrimary, icon = Icons.Default.Share) {
                            Toast.makeText(context, "Sharing link...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            EqubButton(
                text = "Share App Link",
                onClick = {
                    Toast.makeText(context, "App invitation link shared!", Toast.LENGTH_SHORT).show()
                },
                testTag = "share_app_link_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ShareOptionCircle(
    name: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = color,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            fontSize = 11.sp,
            color = EqubTextSecondary
        )
    }
}
