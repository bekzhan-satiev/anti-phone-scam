package kg.digitalshield.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.TableCell(
    modifier: Modifier = Modifier,
    text: String,
    weight: Float,
    textAlign: TextAlign = TextAlign.Left,
    textColor: Color
) {
    Text(
        text = text,
        modifier = modifier.then(
            Modifier
                .weight(weight)
                .padding(8.dp)
        ),
        textAlign = textAlign,
        color = textColor
    )
}