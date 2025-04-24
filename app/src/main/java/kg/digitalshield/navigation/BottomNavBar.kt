package kg.digitalshield.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kg.digitalschield.R
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun BottomNavBar(
    navController: NavController
) {
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(1) }

    val navItems = listOf(
        NavigationItem(
            title = stringResource(id = R.string.search),
            icon = Icons.Default.Search,
            route = Screen.Search.route
        ),
        NavigationItem(
            title = stringResource(id = R.string.home),
            icon = Icons.Default.Home,
            route = Screen.Home.route
        ),
        NavigationItem(
            title = stringResource(id = R.string.stat),
            icon = Icons.Default.DateRange,
            route = Screen.Statistic.route
        )
    )

    NavigationBar(
        modifier = Modifier.height(50.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route) {
                        // Pop up to the root of the navigation graph
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                            saveState = true // Remove the start destination from the back stack
                        }
                        launchSingleTop = true // Avoid multiple copies of the same destination
                        restoreState = true
                    }
                }, icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}


data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)