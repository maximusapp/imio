package com.globaldevmax.app.imio.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop

private const val MAX_ANSWER_LENGTH = 3

@Composable
fun ParentVerificationDialog(
    onConfirmed: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    multiplicationRange: IntRange = 1..10,
    title: String = stringResource(R.string.parent_challenge_title),
    actionText: String = stringResource(R.string.action_submit),
    animationResId: Int? = null,
    showDismissButton: Boolean = true,
    dismissible: Boolean = true
) {
    val challenge = remember(multiplicationRange) {
        multiplicationRange
            .flatMap { left -> multiplicationRange.map { right -> left to right } }
            .random()
    }
    var answer by remember { mutableStateOf("") }
    var showWrongAnswer by remember { mutableStateOf(false) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val submitAnswer: () -> Unit = {
        if (answer.toIntOrNull() == challenge.first * challenge.second) {
            onConfirmed()
        } else {
            showWrongAnswer = true
        }
    }

    val appendDigit: (String) -> Unit = { digit ->
        if (answer.length < MAX_ANSWER_LENGTH) {
            answer += digit
            showWrongAnswer = false
        }
    }

    val removeLastDigit: () -> Unit = {
        if (answer.isNotEmpty()) {
            answer = answer.dropLast(1)
            showWrongAnswer = false
        }
    }

    if (isLandscape) {
        Dialog(
            onDismissRequest = {
                if (dismissible) onDismiss()
            },
            properties = DialogProperties(
                dismissOnBackPress = dismissible,
                dismissOnClickOutside = dismissible,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 360.dp),
                shape = RoundedCornerShape(28.dp),
                color = ImioGradientBottom
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 280.dp, max = 360.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp, end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ParentVerificationTitle(
                            title = title,
                            showDismissButton = showDismissButton,
                            onDismiss = onDismiss
                        )

                        if (animationResId != null) {
                            LottieIcon(
                                animationResId = animationResId,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(96.dp)
                            )
                        }

                        Text(
                            text = stringResource(
                                R.string.parent_challenge_question,
                                challenge.first,
                                challenge.second
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ParentVerificationAnswerDisplay(answer = answer)

                        if (showWrongAnswer) {
                            Text(
                                text = stringResource(R.string.parent_challenge_wrong_answer),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        ImioActionButton(
                            text = actionText,
                            onClick = submitAnswer,
                            modifier = Modifier.fillMaxWidth(),
                            width = Dp.Unspecified,
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }

                    ParentVerificationNumericKeypad(
                        onDigitClick = appendDigit,
                        onBackspaceClick = removeLastDigit,
                        modifier = Modifier
                            .width(188.dp)
                            .fillMaxHeight()
                            .background(ImioGradientTop.copy(alpha = 0.22f))
                            .padding(12.dp)
                    )
                }
            }
        }
    } else {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                if (dismissible) onDismiss()
            },
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = dismissible,
                dismissOnClickOutside = dismissible
            ),
            containerColor = ImioGradientBottom,
            shape = RoundedCornerShape(28.dp),
            title = {
                ParentVerificationTitle(
                    title = title,
                    showDismissButton = showDismissButton,
                    onDismiss = onDismiss
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    if (animationResId != null) {
                        LottieIcon(
                            animationResId = animationResId,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.parent_challenge_question,
                            challenge.first,
                            challenge.second
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ParentVerificationAnswerField(
                        value = answer,
                        onValueChange = {
                            answer = it.filter(Char::isDigit).take(MAX_ANSWER_LENGTH)
                            showWrongAnswer = false
                        }
                    )
                    if (showWrongAnswer) {
                        Text(
                            text = stringResource(R.string.parent_challenge_wrong_answer),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                ImioActionButton(
                    text = actionText,
                    onClick = submitAnswer,
                    modifier = Modifier.fillMaxWidth(),
                    width = 320.dp,
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

@Composable
private fun ParentVerificationTitle(
    title: String,
    showDismissButton: Boolean,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (showDismissButton) 36.dp else 12.dp)
        )
        if (showDismissButton) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_clear),
                    contentDescription = stringResource(R.string.action_close),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ParentVerificationAnswerDisplay(answer: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ImioGradientTop.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.12f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = answer.ifEmpty {
                stringResource(R.string.parent_challenge_answer_label)
            },
            color = if (answer.isEmpty()) {
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.46f)
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ParentVerificationNumericKeypad(
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫")
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    when (key) {
                        "" -> Spacer(modifier = Modifier.weight(1f))
                        "⌫" -> ParentVerificationKeypadKey(
                            label = key,
                            onClick = onBackspaceClick,
                            modifier = Modifier.weight(1f)
                        )
                        else -> ParentVerificationKeypadKey(
                            label = key,
                            onClick = { onDigitClick(key) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentVerificationKeypadKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(
                color = Color.White.copy(alpha = 0.14f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ParentVerificationAnswerField(
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ImioGradientTop.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.12f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 18.dp, vertical = 14.dp),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = stringResource(R.string.parent_challenge_answer_label),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.46f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            innerTextField()
        }
    )
    Spacer(modifier = Modifier.height(2.dp))
}
