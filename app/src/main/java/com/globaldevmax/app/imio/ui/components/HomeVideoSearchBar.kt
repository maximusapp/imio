package com.globaldevmax.app.imio.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globaldevmax.app.imio.R
import com.globaldevmax.app.imio.ui.theme.ImioGradientBottom
import com.globaldevmax.app.imio.ui.theme.ImioGradientTop
import com.globaldevmax.app.imio.ui.theme.ImioOnBackground
import com.globaldevmax.app.imio.ui.theme.Pink
import com.globaldevmax.app.imio.ui.theme.Purple40
import com.globaldevmax.app.imio.ui.theme.Purple80

private val SearchBarShape = RoundedCornerShape(22.dp)

@Composable
fun HomeVideoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0.99f,
        animationSpec = tween(durationMillis = 180),
        label = "searchBarScale"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) {
            ImioGradientBottom.copy(alpha = 0.96f)
        } else {
            ImioGradientBottom.copy(alpha = 0.88f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "searchBarBackground"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else if (query.isNotEmpty()) 0.72f else 0.45f,
        animationSpec = tween(durationMillis = 200),
        label = "searchBarBorderAlpha"
    )
    val premiumBorderBrush = Brush.linearGradient(
        colors = listOf(
            ImioGradientBottom.copy(alpha = borderAlpha),
            Purple40.copy(alpha = borderAlpha),
            Pink.copy(alpha = borderAlpha),
            ImioGradientTop.copy(alpha = borderAlpha)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isFocused) 10.dp else 6.dp,
                shape = SearchBarShape,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.24f)
            )
            .clip(SearchBarShape)
            .border(
                width = if (isFocused) 1.5.dp else 1.dp,
                brush = premiumBorderBrush,
                shape = SearchBarShape
            )
            .background(backgroundColor)
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
                    .alpha(if (isFocused || query.isNotEmpty()) 1f else 0.82f)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    textStyle = TextStyle(
                        color = Purple80,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(Purple80),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions.Default,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (query.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.home_search_hint),
                                    color = ImioOnBackground.copy(alpha = 0.55f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 20.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clear),
                        contentDescription = stringResource(R.string.home_search_clear),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
