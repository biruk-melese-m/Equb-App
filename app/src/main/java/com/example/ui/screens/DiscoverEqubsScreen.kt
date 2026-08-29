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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EqubItem
import com.example.data.EqubRepository
import com.example.ui.components.EqubButton
import com.example.ui.components.EqubTopBar
import com.example.ui.components.equbTextFieldColors
import com.example.ui.theme.*

@Composable
fun DiscoverEqubsScreen(
    onSelectEqub: (EqubItem) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val equbs by EqubRepository.equbs.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredEqubs = equbs.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (it.title.contains(searchQuery, ignoreCase = true) || it.monthlyContribution.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Discover Equbs",
                onBack = onBack,
                rightIcon = Icons.Outlined.FilterList,
                onRightAction = { /* filter action */ }
            )
        },
        modifier = modifier.testTag("discover_equbs_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Equbs by title or amount...") },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = EqubTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EqubTextSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = EqubTextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = equbTextFieldColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("discover_search_bar")
                )
            }

            // Category Filter Chips
            item {
                val categories = listOf("All", "Savings", "Business", "House", "Car")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EqubPrimary else Color.White,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, EqubBorder),
                            modifier = Modifier
                                .clickable { selectedCategory = category }
                                .testTag("category_chip_$category")
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else EqubTextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            if (filteredEqubs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matching Equbs found.",
                            color = EqubTextSecondary,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // Equb Cards
            items(filteredEqubs) { equb ->
                DiscoverEqubCard(
                    equb = equb,
                    onViewDetails = { onSelectEqub(equb) }
                )
            }
        }
    }
}

@Composable
fun DiscoverEqubCard(
    equb: EqubItem,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, iconBg) = when (equb.category) {
        "Savings" -> Pair(Icons.Default.Savings, Color(0xFFF3E8FF))
        "Business" -> Pair(Icons.Default.TrendingUp, Color(0xFFE0F2FE))
        "House" -> Pair(Icons.Default.Home, Color(0xFFDCFCE7))
        else -> Pair(Icons.Default.DirectionsCar, Color(0xFFFEF3C7))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(16.dp))
            .testTag("discover_card_${equb.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = EqubPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = equb.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = EqubTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Monthly Contribution: ${equb.monthlyContribution}",
                        fontSize = 13.sp,
                        color = EqubTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Members: ${equb.currentMembers}/${equb.maxMembers}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = EqubTextSecondary
                )

                Button(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EqubPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("view_details_button_${equb.id}")
                ) {
                    Text(
                        text = "View Details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
