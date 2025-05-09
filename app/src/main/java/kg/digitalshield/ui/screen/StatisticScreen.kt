package kg.digitalshield.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ir.ehsannarmani.compose_charts.models.Pie
import kg.digitalschield.R
import kg.digitalshield.db.CallStatus
import kg.digitalshield.ui.component.PieChartStat
import kg.digitalshield.viewmodel.CallViewModel

@Composable
fun StatisticScreen(callViewModel: CallViewModel = hiltViewModel()) {
    val calls by callViewModel.calls.observeAsState(emptyList())

    val statusCounts = calls.groupingBy { it.callStatus }.eachCount()

    val pieData = listOf(
        Pie(
            label = stringResource(R.string.safe),
            data = statusCounts[CallStatus.SAFE]?.toDouble() ?: 0.0,
            color = Color.Green
        ),
        Pie(
            label = stringResource(R.string.suspicious),
            data = statusCounts[CallStatus.SUSPICIOUS]?.toDouble() ?: 0.0,
            color = Color.Yellow
        ),
        Pie(
            label = stringResource(R.string.blocked),
            data = statusCounts[CallStatus.BLOCKED]?.toDouble() ?: 0.0,
            color = Color.Red
        ),
    )

    PieChartStat(pieData)
}


