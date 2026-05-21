package vegabobo.dsusideloader.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vegabobo.dsusideloader.ui.screen.about_fork.ForkAboutScreen
import vegabobo.dsusideloader.ui.screen.downloads.DownloadsScreen
import vegabobo.dsusideloader.ui.screen.flash.FlashScreen
import vegabobo.dsusideloader.ui.theme.stellar.NavActive
import vegabobo.dsusideloader.ui.theme.stellar.NavInactive

private data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
)

private val bottomNavItems = listOf(
    BottomNavItem("Flash", Icons.Filled.Bolt, Icons.Outlined.Bolt, "flash"),
    BottomNavItem("Downloads", Icons.Filled.Download, Icons.Outlined.Download, "downloads"),
    BottomNavItem("About", Icons.Filled.Info, Icons.Outlined.Info, "about_fork"),
)

@Composable
fun StellarNavigation() {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            selectedTab = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavActive,
                            selectedTextColor = NavActive,
                            unselectedIconColor = NavInactive,
                            unselectedTextColor = NavInactive,
                            indicatorColor = NavActive.copy(alpha = 0.15f),
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "downloads",
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            composable("flash") { FlashScreen() }
            composable("downloads") { DownloadsScreen() }
            composable("about_fork") { ForkAboutScreen() }
        }
    }
}
