package kg.digitalshield.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kg.digitalschield.R
import kg.digitalshield.dto.request.CheckRequest
import kg.digitalshield.viewmodel.CheckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckScreen(checkViewModel: CheckViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var lastSearchedNumber by remember { mutableStateOf("") }

    val checkState by checkViewModel.checkState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.search_db),
            fontWeight = FontWeight.Bold
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                val request = CheckRequest(phoneNumber = searchQuery)
                checkViewModel.isFraudNumber(request)
                lastSearchedNumber = searchQuery
                searchQuery = ""
            },
            active = false,
            onActiveChange = {},
            placeholder = { Text(text = stringResource(id = R.string.enter_phone_number)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(id = R.string.search_icon_description),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {}

        Spacer(modifier = Modifier.height(20.dp))

        when {
            checkState.isInProgress -> {
                Text(text = stringResource(id = R.string.check_for))
                Text(text = lastSearchedNumber)
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            checkState.isFraudNumber != null -> {
                val isFraud = checkState.isFraudNumber!!

                Text(
                    text = lastSearchedNumber,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(id = getTextByFraudStatus(isFraud)),
                    color = getTextColorByFraudStatus(isFraud),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Image(
                    painter = painterResource(id = getImageByFraudStatus(checkState.isFraudNumber!!)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            checkState.error != null -> {
                Text(
                    text = checkState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

private fun getTextByFraudStatus(isFraud: Boolean): Int {
    return if (isFraud) R.string.number_is_dangerous else R.string.number_is_safe
}

private fun getTextColorByFraudStatus(isFraud: Boolean): Color {
    return if (isFraud) Color.Red else Color.Green
}

private fun getImageByFraudStatus(isFraud: Boolean): Int {
    return if (isFraud) R.drawable.block else R.drawable.safe
}
