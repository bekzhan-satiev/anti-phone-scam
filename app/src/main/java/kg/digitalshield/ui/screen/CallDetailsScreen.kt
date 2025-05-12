package kg.digitalshield.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kg.digitalschield.R
import kg.digitalshield.db.CallStatus
import kg.digitalshield.dto.CallDetailState
import kg.digitalshield.viewmodel.CallViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallDetailsScreen(callId: Int, callViewModel: CallViewModel = hiltViewModel()) {

    Log.d("id ", callId.toString())
    val state by callViewModel.state.collectAsState()

    LaunchedEffect(callId) {
        callViewModel.loadCallDetails(callId)
    }

    Column {

        when (val current = state) {
            is CallDetailState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            is CallDetailState.Success -> {
                Text(
                    text = stringResource(id = R.string.call_details),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = R.string.number))
                    Text(text = current.call.phoneNumber)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = R.string.call_date))
                    Text(text = formatDate(current.call.callDate))
                }


                if (current.call.callStatus == CallStatus.SUSPICIOUS) {

                    Text(
                        text = stringResource(id = R.string.suspicious_call),
                        fontSize = 26.sp,
                        color = Color.Yellow
                    )

                    Text(
                        text = stringResource(id = R.string.suspicious_phrases),
                        fontSize = 20.sp
                    )

                    val phrases = current.call.suspiciousPhrases.split(",").map { it.trim() }

                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        itemsIndexed(phrases) { index, phrase ->
                            Text(text = "${index + 1} - $phrase", color = Color(0xFFE5A000))
                        }
                    }

                } else {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.schield),
                            contentDescription = stringResource(id = R.string.shield_image_description),
                            alpha = 0.1f,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxSize()
                        )

                        Image(
                            painter = painterResource(id = getImageIdBasedOnCallStatus(current.call.callStatus)),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                        )


                    }
                    Text(
                        text = stringResource(id = getStringIdBasedOnCallStatus(current.call.callStatus)),
                        fontSize = 26.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                }
            }

            is CallDetailState.Error -> {
                current.message?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }


    }
}

private fun formatDate(date: Date): String {
    val pattern = "d MMMM HH:mm"
    val format = SimpleDateFormat(pattern, Locale("ru", "RU"))

    return format.format(date)
}

private fun getImageIdBasedOnCallStatus(callStatus: CallStatus): Int {
    return when (callStatus) {
        CallStatus.SAFE -> R.drawable.safe
        CallStatus.BLOCKED -> R.drawable.block
        CallStatus.SUSPICIOUS -> R.drawable.suspicious
    }
}

private fun getStringIdBasedOnCallStatus(callStatus: CallStatus): Int {
    return when (callStatus) {
        CallStatus.SAFE -> R.string.safe_call
        CallStatus.BLOCKED -> R.string.blocked_call
        CallStatus.SUSPICIOUS -> R.string.suspicious_call
    }
}

