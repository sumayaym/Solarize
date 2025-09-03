package no.uio.ifi.in2000.team39.ui.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team39.model.map.SearchState

// This is the search bar component on map screen

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    searchState: SearchState,
    displayName: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 30.dp)
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Søk på adresse", color = MaterialTheme.colorScheme.primary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Kart ikon",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onSearch,
                    enabled = query.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Søk",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { if (query.isNotBlank()) onSearch() }),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary
            )
        )

        if (!displayName.isNullOrBlank()) {
            Text(
                text = "Adresse: $displayName",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        if (searchState is SearchState.Error) {
            Text(
                text = searchState.message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
