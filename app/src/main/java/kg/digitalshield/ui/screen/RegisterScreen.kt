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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kg.digitalschield.R
import kg.digitalshield.navigation.Screen
import kg.digitalshield.ui.component.TopRoundedColumn
import kg.digitalshield.ui.theme.AppTheme
import kg.digitalshield.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }

    val registerState by viewModel.registerState.collectAsState()


    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(0.8f)
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
                    .padding(16.dp),
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
                .weight(1.2f)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = login,
                onValueChange = {
                    login = it
                    viewModel.resetState()
                },
                label = { Text(text = stringResource(id = R.string.phone_number)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.resetState()
                },
                label = { Text(text = stringResource(id = R.string.password)) },
                visualTransformation = PasswordVisualTransformation(mask = '*'),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = passwordRepeat,
                onValueChange = {
                    passwordRepeat = it
                    viewModel.resetState()
                },
                label = { Text(text = stringResource(id = R.string.password_repeat)) },
                visualTransformation = PasswordVisualTransformation(mask = '*'),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            when {
                registerState.isLoading -> {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.registering))
                    }
                }

                registerState.isSuccess -> {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                }

                registerState.error != null -> {
                    Text(
                        text = registerState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                else -> {
                    Button(
                        onClick = { viewModel.register(phoneNumber = login, password = password) },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.to_register))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = stringResource(id = R.string.have_account))
                Text(
                    text = stringResource(id = R.string.to_login),
                    modifier = Modifier.clickable { navController.navigate(Screen.Login.route) },
                    style = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    AppTheme {
        RegisterScreen(modifier = Modifier.fillMaxSize(), rememberNavController())
    }
}