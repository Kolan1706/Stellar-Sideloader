package vegabobo.dsusideloader.ui.components.stellarcards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.theme.stellar.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallationParamsCard() {
    var forceWipe by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    val partitionSizes = listOf("16 GB (Minimal)", "32 GB (Medium Setup)", "48 GB (Extended)", "64 GB (Maximum)")
    var selectedSize by remember { mutableStateOf(partitionSizes[1]) }

    StellarCard(
        title = "\u2699\uFE0F Installation Parameters",
    ) {
        Spacer(Modifier.height(12.dp))

        Text(
            text = "Format Userdata Options",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "\u26A0\uFE0F Force Wipe & Format Data",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text(
                    text = "(Recommended)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Warning,
                    fontWeight = FontWeight.Medium,
                )
            }
            Switch(
                checked = forceWipe,
                onCheckedChange = { forceWipe = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                ),
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Performs a clean ext4/f2fs format on the virtual DSU userdata partition to prevent bootloops from old app fragments.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedSize,
                onValueChange = {},
                readOnly = true,
                label = { Text("Virtual Partition Size") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                partitionSizes.forEach { size ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = size,
                                fontWeight = if (size == selectedSize) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        },
                        onClick = {
                            selectedSize = size
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
