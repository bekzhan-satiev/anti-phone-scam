package kg.digitalshield.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kg.digitalschield.R
import java.util.Locale

@Composable
fun PieChartStat(pieData: List<Pie>) {
    var data by remember { mutableStateOf(pieData) }

    // Watch for pieData changes and update internal state
    if (data != pieData) {
        data = pieData
    }

    val total = data.sumOf { it.data }
    val shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)

    Column(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, Color.Black, shape)
            .clip(shape)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "${stringResource(R.string.all_calls)}: ${total.toInt()}")
            }
        }

        PieChart(
            modifier = Modifier
                .padding(8.dp)
                .size(200.dp),
            data = data,
            onPieClick = { pie ->
                val index = data.indexOf(pie)
                data = data.mapIndexed { i, p -> p.copy(selected = i == index) }
            },
            style = Pie.Style.Stroke(width = 30.dp)
        )

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { pie ->
                val percent = if (total > 0) (pie.data / total) * 100 else 0.0
                StatisticLabel(
                    color = pie.color,
                    text = pie.label,
                    percent = percent,
                    count = pie.data.toInt()
                )
            }
        }
    }
}

@Composable
fun StatisticLabel(color: Color, text: String?, percent: Double, count: Int) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(modifier = Modifier.size(11.dp)) {
                drawCircle(color = color)
            }
            text?.let {
                Text(
                    text = it,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
        Text(
            text = "${String.format(Locale.US, "%.1f", percent)}%",
            fontSize = 13.sp,
        )
        Text(
            text = "Количество: $count",
            fontSize = 10.sp,
        )
    }
}




