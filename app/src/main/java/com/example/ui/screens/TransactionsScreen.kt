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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubStatusBadge
import com.example.ui.components.EqubTopBar
import com.example.ui.theme.*

@Composable
fun AllPaymentTransactionsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by EqubRepository.transactions.collectAsState()
    var dateFilter by remember { mutableStateOf("Jan 1, 2023 – May 31, 2023") }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "All Payment Transactions",
                onBack = onBack,
                rightIcon = Icons.Outlined.CalendarMonth,
                onRightAction = { /* date filter */ }
            )
        },
        modifier = modifier.testTag("all_transactions_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // Filter by Date Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Filter by Date", fontSize = 12.sp, color = EqubTextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = dateFilter, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EqubTextPrimary)
                        }

                        Button(
                            onClick = { /* Select dates dialog */ },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EqubPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Select Dates", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Transaction items
            items(transactions) { tx ->
                TransactionRowCard(transaction = tx)
            }
        }
    }
}

@Composable
fun TransactionRowCard(
    transaction: TransactionItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp))
            .testTag("transaction_row_${transaction.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon: Green check for contribution/verified, Orange for withdrawal/fee
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.isPositive) Color(0xFFEAF9F0) else Color(0xFFFFF2EB)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaction.isPositive) Icons.Default.Check else Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = if (transaction.isPositive) EqubPaidGreen else EqubOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = transaction.type,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )
                    Text(
                        text = transaction.date,
                        fontSize = 12.sp,
                        color = EqubTextSecondary
                    )
                }
            }

            Text(
                text = transaction.amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isPositive) EqubPaidGreen else EqubOrange
            )
        }
    }
}

@Composable
fun MyPaymentHistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val history by EqubRepository.paymentHistory.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredHistory = history.filter {
        selectedFilter == "All" || it.status.equals(selectedFilter, ignoreCase = true)
    }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "My Payment History",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("my_payment_history_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Segmented Tabs: All (selected) | Paid | Pending
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBEBF5))
                    .padding(4.dp)
            ) {
                listOf("All", "Paid", "Pending").forEach { tab ->
                    val isSelected = selectedFilter == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) EqubPrimary else Color.Transparent)
                            .clickable { selectedFilter = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else EqubTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredHistory) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, EqubCardBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Round ${item.roundNumber} - ${item.date}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EqubTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.amount,
                                    fontSize = 13.sp,
                                    color = EqubTextSecondary
                                )
                            }

                            EqubStatusBadge(status = item.status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PayoutHistoryScreen(
    onViewAll: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val payouts by EqubRepository.payoutHistory.collectAsState()

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Payout History",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("payout_history_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Blue-Purple Gradient Hero Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF432A90), Color(0xFF2C1964))
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Text(
                            text = "Total Received: 3,500 ETB",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Number of Payouts: 7",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(payouts) { payout ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFEDE8FA)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (payout.isFamilyCircle) Icons.Default.Groups else Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = EqubPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "${payout.title} | Round ${payout.roundNumber}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EqubTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = payout.date,
                                        fontSize = 12.sp,
                                        color = EqubTextSecondary
                                    )
                                }
                            }

                            Text(
                                text = payout.amount,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EqubPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            EqubButton(
                text = "View All",
                onClick = onViewAll,
                isOutlined = true,
                testTag = "payout_history_view_all_button"
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
