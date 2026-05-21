package vegabobo.dsusideloader.ui.screen.aboutfork

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.components.stellarcards.StellarCard

@Composable
fun ForkAboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StellarCard(
            title = "About Stellar DSU",
        ) {
            Text(
                text = "Stellar DSU Sideloader — a custom fork of DSU Sideloader with a modernized UI, GSI repository browser, and advanced theming capabilities.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        StellarCard(
            title = "Upstream",
        ) {
            Text(
                text = "Based on DSU Sideloader by VegaBobo\nhttps://github.com/VegaBobo/DSU-Sideloader",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        StellarCard(
            title = "Features",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    "Custom accent colors (Teal, Coral, Purple, Emerald, Blue)",
                    "Glassmorphism UI design",
                    "Built-in GSI repository browser",
                    "Format data & partition wipe options",
                    "Bottom navigation with quick access",
                ).forEach { feature ->
                    Text(
                        text = "• $feature",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        StellarCard(
            title = "Disclaimer",
        ) {
            Text(
                text = "This is a community fork. Use at your own risk. Always backup your data before installing GSIs.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
