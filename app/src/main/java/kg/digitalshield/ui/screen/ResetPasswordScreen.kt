package kg.digitalshield.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kg.digitalschield.R
import kg.digitalshield.navigation.Screen
import kg.digitalshield.ui.component.TopRoundedColumn
import kg.digitalshield.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }

    val requestState by viewModel.resetState.collectAsState()

    Column() {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.schield),
                contentDescription = stringResource(id = R.string.shield_image_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxSize()
                    .padding(16.dp)
            )

            Text(
                modifier = Modifier
                    .weight(0.2f),
                text = stringResource(id = R.string.digital_shield),
                style = TextStyle(
                    fontSize = 30.sp,
                )
            )
        }

        TopRoundedColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = { Text(text = stringResource(id = R.string.email)) },
                modifier = Modifier.fillMaxWidth()
            )

            when {
                requestState.isLoading -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }

                requestState.isSuccess -> {
                    navController.navigate(Screen.ResetPassword.route) {
                        popUpTo(Screen.ResetPassword.route) {
                            inclusive = true
                        }
                    }
                }

                requestState.error != null -> {
                    Text(
                        text = requestState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                else -> {
                    Button(
                        onClick = {
                            viewModel.resetPassword(email)
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) { Text(stringResource(id = R.string.reset_password)) }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = stringResource(id = R.string.to_login),
                    modifier = Modifier.clickable { navController.navigate(Screen.Login.route) },
                    style = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }

        }
    }
}