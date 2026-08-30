package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EqubItem
import com.example.data.EqubRepository
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

private val HeaderWineRed = Color(0xFF850E26)
private val BadgeGreenBg = Color(0xFFE8F8EE)
private val BadgeGreenText = Color(0xFF16A34A)

private data class MemberRowData(
    val index: Int,
    val initials: String,
    val name: String,
    val position: String,
    val positionColor: Color,
    val avatarBg: Color,
    val avatarText: Color,
    val isUser: Boolean = false
)

@Composable
fun ActiveEqubScreen(
    initialEqubId: String? = null,
    onViewCycle: () -> Unit = {},
    onViewMembers: () -> Unit = {},
    onViewSchedule: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val equbs by EqubRepository.equbs.collectAsState()
    val joinedEqubs = equbs.filter { it.isUserJoined }
    val myEqubsList = if (joinedEqubs.isNotEmpty()) joinedEqubs else equbs

    // When selectedEqubId is null, show the list of Equbs.
    // When selectedEqubId is non-null, show the specific Equb's details.
    var selectedEqubId by remember(initialEqubId) {
        mutableStateOf<String?>(initialEqubId)
    }

    val selectedEqub = selectedEqubId?.let { id -> equbs.find { it.id == id } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HeaderWineRed)
            .testTag("active_equb_screen_root")
    ) {
        AnimatedContent(
            targetState = selectedEqub,
            transitionSpec = {
                if (targetState != null) {
                    (slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(250))) togetherWith
                            (slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut(tween(200)))
                } else {
                    (slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn(tween(250))) togetherWith
                            (slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(200)))
                }
            },
            label = "EqubScreenTransition"
        ) { currentSelectedEqub ->
            if (currentSelectedEqub == null) {
                // 1. MY EQUB LIST VIEW (List of joined equbs & savings)
                MyEqubsListView(
                    equbs = myEqubsList,
                    onSelectEqub = { equb ->
                        selectedEqubId = equb.id
                    },
                    onBack = onBack
                )
            } else {
                // 2. EQUB DETAIL VIEW (The detailed view matching the requested design)
                EqubDetailView(
                    selectedEqub = currentSelectedEqub,
                    onBack = {
                        if (initialEqubId != null && onBack != null) {
                            onBack()
                        } else {
                            selectedEqubId = null
                        }
                    },
                    onViewMembers = onViewMembers
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. LIST OF EQUBS VIEW
// -------------------------------------------------------------
@Composable
private fun MyEqubsListView(
    equbs: List<EqubItem>,
    onSelectEqub: (EqubItem) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val totalSavedSum = equbs.sumOf { item ->
        item.userSavedAmount
            .replace("ETB", "", ignoreCase = true)
            .replace("Birr", "", ignoreCase = true)
            .replace(",", "")
            .trim()
            .toIntOrNull() ?: 0
    }
    val formattedTotalSaved = "ETB " + NumberFormat.getNumberInstance(Locale.US).format(totalSavedSum)

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP CRIMSON APP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column {
                    Text(
                        text = "My Equb",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "View and manage all the Equbs you are part of.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // MAIN WHITE CANVAS
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = Color(0xFFF8FAFC)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                // TOTAL SAVINGS SUMMARY HERO CARD
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFFBCFE8), RoundedCornerShape(16.dp))
                            .testTag("my_equbs_total_savings_banner"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF2F8)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "TOTAL SAVED ACROSS EQUBS",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9D174D),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = formattedTotalSaved,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF831843)
                                )
                                Text(
                                    text = "Active in ${equbs.size} rotating savings groups",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFCE7F3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Savings,
                                    contentDescription = null,
                                    tint = Color(0xFFBE185D),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }

                // SECTION HEADER
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Equbs (${equbs.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Tap to view details",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // EQUB CARDS LIST
                items(equbs, key = { it.id }) { equb ->
                    MyEqubCardItem(
                        equb = equb,
                        onClick = { onSelectEqub(equb) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyEqubCardItem(
    equb: EqubItem,
    onClick: () -> Unit
) {
    val progress = (equb.currentRound.toFloat() / equb.totalRounds.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("my_equb_card_${equb.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Avatar + Title + Active Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(HeaderWineRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Equb Circle",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = equb.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Contribution: ${equb.monthlyContribution} / round",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = BadgeGreenBg
                ) {
                    Text(
                        text = "Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BadgeGreenText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 2 Key Stats: You Saved & Position
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Saved amount highlight
                Column {
                    Text(
                        text = "YOU SAVED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = equb.userSavedAmount,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7E22CE)
                    )
                }

                // Right: Position Badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "YOUR TURN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = formatPositionOrdinal(equb.userPosition),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                }
            }

            // Progress Bar (Round X of Y)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Round ${equb.currentRound} of ${equb.totalRounds}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% completed",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF0284C7),
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            // Footer Row: Next Payment Due + View Details Arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Next Due: ${equb.nextPaymentDate} (${equb.daysLeftTillDue}D left)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "View Details",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeaderWineRed
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = HeaderWineRed,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. EQUB DETAIL VIEW (Matching the provided UI specifications)
// -------------------------------------------------------------
@Composable
private fun EqubDetailView(
    selectedEqub: EqubItem,
    onBack: () -> Unit,
    onViewMembers: () -> Unit
) {
    // 5 representative members for the Equb table matching the UI design
    val sampleMembers = listOf(
        MemberRowData(
            index = 1,
            initials = "MA",
            name = "Mekdes Alemu",
            position = "1st",
            positionColor = Color(0xFF16A34A),
            avatarBg = Color(0xFFDCFCE7),
            avatarText = Color(0xFF16A34A)
        ),
        MemberRowData(
            index = 2,
            initials = "YOU",
            name = "You (Biruk Melese)",
            position = formatPositionOrdinal(selectedEqub.userPosition),
            positionColor = Color(0xFFEA580C),
            avatarBg = Color(0xFFFEE2E2),
            avatarText = Color(0xFFDC2626),
            isUser = true
        ),
        MemberRowData(
            index = 3,
            initials = "AT",
            name = "Abel Tesfaye",
            position = "3rd",
            positionColor = Color(0xFFD97706),
            avatarBg = Color(0xFFFFEDD5),
            avatarText = Color(0xFFC2410C)
        ),
        MemberRowData(
            index = 4,
            initials = "SG",
            name = "Selamawit G.",
            position = "4th",
            positionColor = Color(0xFF475569),
            avatarBg = Color(0xFFFEF3C7),
            avatarText = Color(0xFFB45309)
        ),
        MemberRowData(
            index = 5,
            initials = "DM",
            name = "Daniel M.",
            position = "5th",
            positionColor = Color(0xFF475569),
            avatarBg = Color(0xFFDBEAFE),
            avatarText = Color(0xFF1D4ED8)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("active_equb_details_screen")
    ) {
        // 1. TOP CRIMSON APP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "My Equb",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "View and manage all the Equbs you are part of.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // 2. MAIN WHITE CANVAS
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = Color.White
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                // HEADER ROW: Avatar + Biruk's Equb + Active Badge
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("equb_header_card"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(HeaderWineRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Equb Group",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = selectedEqub.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.testTag("active_equb_title")
                                )

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = BadgeGreenBg
                                ) {
                                    Text(
                                        text = "Active",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BadgeGreenText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    }
                }

                // 4 STAT METRICS ROW
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("active_equb_metrics_grid"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // 1. Amount
                        MetricColumnItem(
                            icon = Icons.Outlined.Payments,
                            iconBg = Color(0xFFE8F8EE),
                            iconTint = Color(0xFF16A34A),
                            label = "Amount",
                            value = selectedEqub.monthlyContribution,
                            valueColor = Color(0xFF15803D),
                            subtext = "Per Member",
                            modifier = Modifier.weight(1f),
                            testTag = "active_equb_contribution_text"
                        )

                        // 2. Round
                        MetricColumnItem(
                            icon = Icons.Outlined.Sync,
                            iconBg = Color(0xFFE0F2FE),
                            iconTint = Color(0xFF0284C7),
                            label = "Round",
                            value = "${selectedEqub.currentRound} / ${selectedEqub.totalRounds}",
                            valueColor = Color(0xFF0284C7),
                            subtext = "Current Round",
                            modifier = Modifier.weight(1f),
                            testTag = "active_equb_round_text"
                        )

                        // 3. You Saved
                        MetricColumnItem(
                            icon = Icons.Outlined.Savings,
                            iconBg = Color(0xFFF3E8FF),
                            iconTint = Color(0xFF7E22CE),
                            label = "You Saved",
                            value = selectedEqub.userSavedAmount,
                            valueColor = Color(0xFF7E22CE),
                            subtext = "Total Saved",
                            modifier = Modifier.weight(1f),
                            testTag = "active_equb_saved_amount"
                        )

                        // 4. Your Position
                        MetricColumnItem(
                            icon = Icons.Outlined.WorkspacePremium,
                            iconBg = Color(0xFFFEF3C7),
                            iconTint = Color(0xFFD97706),
                            label = "Your Position",
                            value = formatPositionOrdinal(selectedEqub.userPosition),
                            valueColor = Color(0xFFD97706),
                            subtext = if (selectedEqub.userPosition == selectedEqub.currentRound) "Payout Active" else "Next Payout",
                            modifier = Modifier.weight(1f),
                            testTag = "my_position_number"
                        )
                    }
                }

                // NEXT PAYMENT DUE IN CARD
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Section: Next Payment Due In
                            Row(
                                modifier = Modifier.weight(1.35f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = "Due Calendar",
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(38.dp)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                    Text(
                                        text = "Next Payment Due In",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "${selectedEqub.daysLeftTillDue} Days",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                    Text(
                                        text = "Get ready for the next payment.",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            // Vertical Hairline Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(48.dp)
                                    .background(Color(0xFFDBEAFE))
                            )

                            // Right Section: Due Date
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Due Date",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarMonth,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = selectedEqub.nextPaymentDate,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Text(
                                    text = "Tuesday",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // MEMBERS SECTION
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Members Card Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onViewMembers() }
                                    .testTag("active_equb_members_button"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF15803D)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = "Members Roster",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Members (${selectedEqub.maxMembers})",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Tap on a member to view details",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Open Members List",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(13.dp)
                                )
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                            // Table Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.width(28.dp)
                                )
                                Text(
                                    text = "Member Name",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Position",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                            // Member Item Rows
                            sampleMembers.forEach { member ->
                                MemberTableRow(
                                    member = member,
                                    onClick = onViewMembers
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // View All Members Outlined Button
                            OutlinedButton(
                                onClick = onViewMembers,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF16A34A)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF16A34A))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "View All Members",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // SECURITY DETAILS CARD
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFDDD6FE), RoundedCornerShape(12.dp))
                            .testTag("active_equb_security_detail"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7FF)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Security Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF7C3AED)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Security Details",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Security Details",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1B4B)
                                    )
                                    Text(
                                        text = "Your Equb is safe and transparent.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFFEDE9FE), thickness = 1.dp)

                            // 4 Feature Columns
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                // 1. National ID Verification
                                SecurityColumnItem(
                                    icon = Icons.Outlined.Badge,
                                    title = "National ID\nVerification",
                                    subtitle = "All members verified",
                                    modifier = Modifier.weight(1f)
                                )

                                // Hairline Divider
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(60.dp)
                                        .background(Color(0xFFEDE9FE))
                                )

                                // 2. Transaction Approval
                                SecurityColumnItem(
                                    icon = Icons.Outlined.VerifiedUser,
                                    title = "Transaction\nApproval",
                                    subtitle = "All payments reviewed",
                                    modifier = Modifier.weight(1f)
                                )

                                // Hairline Divider
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(60.dp)
                                        .background(Color(0xFFEDE9FE))
                                )

                                // 3. Admin Monitoring
                                SecurityColumnItem(
                                    icon = Icons.Outlined.AdminPanelSettings,
                                    title = "Admin\nMonitoring",
                                    subtitle = "Equb is monitored",
                                    modifier = Modifier.weight(1f)
                                )

                                // Hairline Divider
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(60.dp)
                                        .background(Color(0xFFEDE9FE))
                                )

                                // 4. Data Protection
                                SecurityColumnItem(
                                    icon = Icons.Outlined.Lock,
                                    title = "Data\nProtection",
                                    subtitle = "Your data is secure",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Bottom Purple Pill Banner
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF3E8FF),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF6D28D9),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "This Equb follows secure practices to protect all members and funds.",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF6D28D9),
                                        textAlign = TextAlign.Center
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

// -------------------------------------------------------------
// HELPER COMPOSABLES & FORMATTERS
// -------------------------------------------------------------
private fun formatPositionOrdinal(position: Int): String {
    return when (position) {
        1 -> "1st"
        2 -> "2nd"
        3 -> "3rd"
        else -> "${position}th"
    }
}

@Composable
private fun MetricColumnItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String,
    valueColor: Color,
    subtext: String,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Text(
            text = value,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier
        )

        Text(
            text = subtext,
            fontSize = 10.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun MemberTableRow(
    member: MemberRowData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${member.index}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.width(28.dp)
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(member.avatarBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.initials,
                fontSize = if (member.initials.length > 2) 9.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                color = member.avatarText
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = member.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = member.position,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = member.positionColor
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

@Composable
private fun SecurityColumnItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6D28D9),
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B),
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )

        Text(
            text = subtitle,
            fontSize = 8.5.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            lineHeight = 10.5.sp
        )
    }
}
