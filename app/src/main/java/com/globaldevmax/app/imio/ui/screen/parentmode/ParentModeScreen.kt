package com.globaldevmax.app.imio.ui.screen.parentmode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.components.ImioBackButton

@Composable
fun ParentModeScreen(
    isParentModeActive: Boolean,
    allowedMinutes: String,
    onParentModeActiveChange: (Boolean) -> Unit,
    onAllowedMinutesChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        ImioBackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 28.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(top = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.parent_mode_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.parent_mode_toggle),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = isParentModeActive,
                    onCheckedChange = onParentModeActiveChange
                )
            }
            if (isParentModeActive) {
                OutlinedTextField(
                    value = allowedMinutes,
                    onValueChange = { value ->
                        onAllowedMinutesChange(value.filter(Char::isDigit))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.parent_mode_minutes_label)) },
                    supportingText = {
                        Text(text = stringResource(R.string.parent_mode_minutes_support))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}
