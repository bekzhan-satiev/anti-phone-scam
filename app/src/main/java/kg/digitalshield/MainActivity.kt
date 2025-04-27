package kg.digitalshield

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import dagger.hilt.android.AndroidEntryPoint
import kg.digitalshield.db.CallViewModel
import kg.digitalshield.navigation.BottomNavBar
import kg.digitalshield.navigation.Screen
import kg.digitalshield.ui.screen.CallDetailsScreen
import kg.digitalshield.ui.screen.HomeScreen
import kg.digitalshield.ui.screen.LoginScreen
import kg.digitalshield.ui.screen.RegisterScreen
import kg.digitalshield.ui.screen.SearchScreen
import kg.digitalshield.ui.screen.StatisticScreen
import kg.digitalshield.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // "context" must be an Activity, Service or Application object from your app.
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }

        val serviceIntent = Intent(this, BackgroundRecordingService::class.java)
        startForegroundService(serviceIntent)

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                var showBottomBar by rememberSaveable { mutableStateOf(true) }
                showBottomBar = when (navBackStackEntry?.destination?.route) {
                    Screen.Login.route -> false
                    Screen.Register.route -> false
                    else -> true
                }

                // Use Hilt to inject CallViewModel
                val callViewModel: CallViewModel = viewModel()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) BottomNavBar(navController = navController)
                    }) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {
                        composable(route = Screen.Login.route) { LoginScreen(navController = navController) }
                        composable(route = Screen.Home.route) {
                            HomeScreen(
                                navController = navController,
                                callViewModel
                            )
                        }
                        composable(route = Screen.Register.route) { RegisterScreen(navController = navController) }
                        composable(route = Screen.Statistic.route) { StatisticScreen(callViewModel = callViewModel) }
                        composable(route = Screen.Search.route) { SearchScreen() }
                        composable(
                            route = Screen.CallDetailed.route,

                            ) {
                            CallDetailsScreen(callViewModel = callViewModel)
                        }

                    }
                }
            }
        }
    }

}

