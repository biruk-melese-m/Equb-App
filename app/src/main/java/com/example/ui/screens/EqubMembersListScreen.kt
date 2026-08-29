package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.EqubRepository
import com.example.data.Member
import com.example.ui.components.EqubAvatar
import com.example.ui.components.EqubStatusBadge
import com.example.ui.components.EqubTopBar
import com.example.ui.components.equbTextFieldColors
import com.example.ui.theme.*

@Composable
fun EqubMembersListScreen(
    onSelectMember: (Member) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val members by EqubRepository.members.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredMembers = members.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
    }

    Scaffold(
        containerColor = EqubBackground,
        topBar = {
            EqubTopBar(
                title = "Members (${members.size}/20)",
                onBack = onBack
            )
        },
        modifier = modifier.testTag("equb_members_list_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search members...") },
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
                shape = RoundedCornerShape(12.dp),
                colors = equbTextFieldColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_members_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMembers) { member ->
                    MemberRowItem(
                        member = member,
                        onClick = { onSelectMember(member) }
                    )
                }
            }
        }
    }
}

@Composable
fun MemberRowItem(
    member: Member,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, EqubCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("member_row_${member.id}"),
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
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                EqubAvatar(
                    name = member.name,
                    bgColor = Color(member.initialColor),
                    modifier = Modifier.size(44.dp)
                )
                Column {
                    Text(
                        text = member.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EqubTextPrimary
                    )
                    Text(
                        text = "Turn: ${member.rotationPosition}",
                        fontSize = 12.sp,
                        color = EqubTextSecondary
                    )
                }
            }

            EqubStatusBadge(
                status = if (member.isPaid) "Paid" else "Pending..."
            )
        }
    }
}
