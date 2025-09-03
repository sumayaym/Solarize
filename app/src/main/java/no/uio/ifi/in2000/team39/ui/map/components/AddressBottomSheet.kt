package no.uio.ifi.in2000.team39.ui.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team39.model.map.GeocodingResult
import no.uio.ifi.in2000.team39.ui.map.components.AddressInputValidator as Validator

// This is for bottomSheet on map screen

@Composable
fun AddressBottomSheet(
    geocodingResult: GeocodingResult?,
    onSave: (String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var angle by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }

    val address = geocodingResult?.address
    val display = geocodingResult?.displayName ?: ""
    val displayWords = display.split(",").firstOrNull()?.trim()?.split(" ") ?: emptyList()

    val parsedNumber =
        address?.houseNumber ?: displayWords.lastOrNull { it.any { c -> c.isDigit() } }
    val parsedRoad =
        address?.road ?: displayWords.dropLast(1).joinToString(" ").takeIf { it.isNotBlank() }

    val isLengthValid = Validator.isPositiveDouble(length)
    val isWidthValid = Validator.isPositiveDouble(width)
    val isAngleValid = Validator.isAngleValid(angle)
    val isDirectionValid = Validator.isDirectionValid(direction)
    val isAddressValid = !parsedRoad.isNullOrBlank()
    val isValid =
        isAddressValid && isLengthValid && isWidthValid && isAngleValid && isDirectionValid

    val fullAddress = buildString {
        if (!parsedRoad.isNullOrBlank()) {
            append(parsedRoad)
            if (!parsedNumber.isNullOrBlank()) {
                append(" $parsedNumber")
            }
        }
        address?.let { addr ->
            if (!addr.postcode.isNullOrBlank()) append(", ${addr.postcode}")
            if (!addr.city.isNullOrBlank()) append(", ${addr.city}")
            else if (!addr.town.isNullOrBlank()) append(", ${addr.town}")
            else if (!addr.village.isNullOrBlank()) append(", ${addr.village}")
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Legg til bolig",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        if (fullAddress.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Adresse: $fullAddress",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = length,
            onValueChange = {
                if (Validator.isDigitsOrDot(it)) length = it
            },
            label = { Text("Taklengde (m)") },
            isError = !isLengthValid && length.isNotBlank(),
            supportingText = {
                if (!isLengthValid && length.isNotBlank())
                    Text("Taklengde må være positiv")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = width,
            onValueChange = {
                if (Validator.isDigitsOrDot(it)) width = it
            },
            label = { Text("Takbredde (m)") },
            isError = !isWidthValid && width.isNotBlank(),
            supportingText = {
                if (!isWidthValid && width.isNotBlank())
                    Text("Takbredde må være positiv")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = angle,
            onValueChange = {
                if (Validator.isDigitsOnly(it)) angle = Validator.clampAngle(it)
            },
            label = { Text("Takvinkel (i grader)") },
            supportingText = {
                if (angle == "180") Text("180° er maks for takvinkel")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = direction,
            onValueChange = {
                if (Validator.isDigitsOnly(it)) direction = Validator.clampDirection(it)
            },
            label = { Text("Takretning (N = 0°, Ø = 90°, S = 180°, V = 270°)") },
            supportingText = {
                if (direction == "359") Text("359° er maks for kompassretning")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Avbryt", color = MaterialTheme.colorScheme.primary)
            }

            OutlinedButton(
                onClick = {
                    val compassDirection = direction.toFloatOrNull() ?: 0f
                    val directionPVGIS = compassToPVGIS(compassDirection)
                    onSave(fullAddress, length, width, angle, directionPVGIS.toString())
                },
                enabled = isValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Lagre", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

fun compassToPVGIS(compass: Float): Float {
    return (180f - compass + 360f) % 360f
}