package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EqubDivider
import com.example.ui.theme.EqubPrimary
import com.example.ui.theme.EqubTextSecondary

enum class EqubTab(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    EXPLORE("Explore", Icons.Filled.Explore, Icons.Outlined.Explore, "nav_explore"),
    MY_EQUBS("My Equbs", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_my_equbs"),
    PAYMENTS("Payments", Icons.Filled.CreditCard, Icons.Outlined.CreditCard, "nav_payments"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

@Composable
fun EqubBottomNavBar(
    selectedTab: EqubTab,
    onTabSelected: (EqubTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("equb_bottom_nav"),
        containerColor = Color.White,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        EqubTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EqubPrimary,
                    selectedTextColor = EqubPrimary,
                    unselectedIconColor = EqubTextSecondary,
                    unselectedTextColor = EqubTextSecondary,
                    indicatorColor = Color(0xFFEDE8FA)
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}
