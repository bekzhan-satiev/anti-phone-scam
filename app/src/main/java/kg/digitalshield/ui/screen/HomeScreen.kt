package kg.digitalshield.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kg.digitalschield.R
import kg.digitalshield.ui.component.CallsTable
import kg.digitalshield.ui.component.LabelMarker
import kg.digitalshield.ui.component.TopRoundedColumn
import kg.digitalshield.viewmodel.CallViewModel
import kg.digitalshield.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    callViewModel: CallViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val phoneNumber by homeViewModel.phoneNumber.collectAsState()
    LaunchedEffect(Unit) {
        homeViewModel.loadPhoneNumber()
    }

    var searchQuery by remember { mutableStateOf("") }
    var searchActiveness by remember { mutableStateOf(false) }

    val calls by callViewModel.calls.observeAsState()

    val filteredCalls = calls?.filter { it.phoneNumber.contains(searchQuery, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.schield),
                contentDescription = stringResource(id = R.string.shield_image_description),
                alpha = 0.3f,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                phoneNumber?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    LabelMarker(
                        description = stringResource(id = R.string.safe),
                        color = Color.Black
                    )

                    LabelMarker(
                        description = stringResource(id = R.string.blocked),
                        color = Color.Red
                    )

                    LabelMarker(
                        description = stringResource(id = R.string.suspicious),
                        color = Color(0xFFE5A000)
                    )

                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { searchActiveness = false },
                        active = false,
                        onActiveChange = { searchActiveness = it },
                        placeholder = { Text(text = stringResource(id = R.string.enter_phone_number)) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(id = R.string.search_icon_description),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                    ) {
                    }
                }
            }

        }
        TopRoundedColumn(
            modifier = Modifier
                .weight(0.6f, fill = true)
                .background(MaterialTheme.colorScheme.onPrimary)
                .fillMaxSize()
        ) {
            (if (searchQuery.isNotEmpty()) filteredCalls else calls)?.let {
                CallsTable(
                    calls = it,
                    navController = navController,
                    callViewModel = callViewModel
                )
            }
        }

    }
}

