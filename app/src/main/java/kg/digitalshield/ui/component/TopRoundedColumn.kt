package kg.digitalshield.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kg.digitalshield.ui.theme.AppTheme

@Composable
fun TopRoundedColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Preview(showSystemUi = true)
@Composable
fun TopRoundedColumnPreview() {
    AppTheme {
        TopRoundedColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {

        }
    }
}