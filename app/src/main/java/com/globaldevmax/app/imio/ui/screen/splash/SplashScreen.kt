package com.globaldevmax.app.imio.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.network.connectivity.ConnectivityChecker
import com.globaldevmax.app.imio.ui.components.ImioGradientLinearLoader

@Composable
fun SplashScreen(
    connectivityChecker: ConnectivityChecker,
    onInternetAvailable: () -> Unit,
    canProceed: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isChecking by remember { mutableStateOf(true) }
    var hasConnectionError by remember { mutableStateOf(false) }
    var retryKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(retryKey, canProceed) {
        isChecking = true
        hasConnectionError = false

        if (!canProceed) {
            return@LaunchedEffect
        }

        if (connectivityChecker.hasInternetConnection()) {
            onInternetAvailable()
        } else {
            isChecking = false
            hasConnectionError = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_imio),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.width(220.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        if (isChecking) {
            ImioGradientLinearLoader(
                width = 220.dp
            )
        }

        if (hasConnectionError) {
            Text(
                text = stringResource(R.string.splash_no_internet),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { retryKey++ }) {
                Text(text = stringResource(R.string.action_retry))
            }
        }
    }
}
