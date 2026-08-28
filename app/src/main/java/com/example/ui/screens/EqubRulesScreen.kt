package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun EqubRulesScreen(
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAccepted by remember { mutableStateOf(true) }

    val guidelines = listOf(
        "1. Contribution Deadlines" to "All members must contribute their fixed amount by the 1st of every month. Late contributions will incur a fee.",
        "2. Late Fee Policies" to "A late fee of 10% will be charged for contributions made after the 5th of the month. Repeated late payments may lead to removal.",
        "3. Payout Rotation Procedures" to "Payouts are distributed on a predefined rotation schedule, usually by the 10th of the month. Members cannot change their rotation turn without unanimous consent.",
        "4. Membership" to "Membership is voluntary but binding. A 30-day notice is required to withdraw.",
        "5. Dispute Resolution" to "Any disputes will be mediated by the Equb committee. The committee's decision is final."
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            EqubTopBar(
                title = "Equb Rules and Terms",
                onBack = onBack,
                rightIcon = Icons.Outlined.Share,
                onRightAction = { /* share rules */ }
            )
        },
        modifier = modifier.testTag("equb_rules_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Association Guidelines",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = EqubTextPrimary,
                modifier = Modifier.testTag("rules_heading")
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(guidelines) { (title, description) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EqubPrimary)
                        )

                        Column {
                            Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EqubTextPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = description,
                                fontSize = 13.sp,
                                color = EqubTextSecondary,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Acceptance Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isAccepted = !isAccepted }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAccepted,
                    onCheckedChange = { isAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = EqubPrimary,
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.testTag("accept_rules_checkbox")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "I accept the rules and terms.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = EqubTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            EqubButton(
                text = "Confirm",
                onClick = onConfirm,
                enabled = isAccepted,
                testTag = "rules_confirm_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
