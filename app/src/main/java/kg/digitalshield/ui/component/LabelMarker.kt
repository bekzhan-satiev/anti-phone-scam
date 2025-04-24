package kg.digitalshield.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kg.digitalshield.ui.theme.AppTheme

@Composable
fun LabelMarker(modifier: Modifier = Modifier, description: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Text(text = description)
        Canvas(modifier = Modifier.size(20.dp)) {
            drawCircle(color = color)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LabelMarkerPreview() {
    AppTheme {
        Column {
            LabelMarker(
                modifier = Modifier.fillMaxWidth(),
                "Заблокированные",
                MaterialTheme.colorScheme.error
            )
            LabelMarker(
                modifier = Modifier.fillMaxWidth(),
                "Заблокированные",
                MaterialTheme.colorScheme.error
            )

        }
    }
}