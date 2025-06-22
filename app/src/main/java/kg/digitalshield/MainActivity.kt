package kg.digitalshield

import android.app.Activity
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import dagger.hilt.android.AndroidEntryPoint
import kg.digitalshield.navigation.BottomNavBar
import kg.digitalshield.navigation.Screen
import kg.digitalshield.ui.screen.CallDetailsScreen
import kg.digitalshield.ui.screen.CheckScreen
import kg.digitalshield.ui.screen.HomeScreen
import kg.digitalshield.ui.screen.LoginScreen
import kg.digitalshield.ui.screen.RegisterScreen
import kg.digitalshield.ui.screen.ResetPasswordScreen
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


            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            val startForRequestRoleResult = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: androidx.activity.result.ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    //  you will get result here in result.data
                    bindMyService()
                }
            }
            startForRequestRoleResult.launch(intent)


        setContent {
            AppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                var showBottomBar by rememberSaveable { mutableStateOf(true) }
                showBottomBar = when (navBackStackEntry?.destination?.route) {
                    Screen.Login.route -> false
                    Screen.Register.route -> false
                    Screen.ResetPassword.route -> false
                    else -> true
                }


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
                            )
                        }
                        composable(route = Screen.Register.route) { RegisterScreen(navController = navController) }
                        composable(route = Screen.Statistic.route) { StatisticScreen() }
                        composable(route = Screen.Search.route) { CheckScreen() }
                        composable(
                            route = Screen.CallDetailed.route,
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.IntType
                                    nullable = false
                                })
                        ) { backStackEntry ->
                            backStackEntry.arguments?.getInt("id")?.let { callId ->
                                CallDetailsScreen(callId = callId)
                            }
                        }
                        composable(route = Screen.ResetPassword.route) { ResetPasswordScreen(navController = navController) }
                    }
                }
            }
        }
    }
    private fun bindMyService(){
        Log.i("MainActivity", "binding my service")
        val mCallServiceIntent = Intent("android.telecom.CallScreeningService")
        mCallServiceIntent.setPackage(applicationContext.packageName)
        val mServiceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                // iBinder is an instance of CallScreeningService.CallScreenBinder
                // CallScreenBinder is an inner class present inside CallScreenService
            }
            override fun onServiceDisconnected(componentName: ComponentName) {}
            override fun onBindingDied(name: ComponentName) {}
        }
        bindService(mCallServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }

}

