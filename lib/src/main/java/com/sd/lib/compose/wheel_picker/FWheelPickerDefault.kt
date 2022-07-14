package com.sd.lib.compose.wheel_picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * The default implementation of focus view in vertical.
 */
@Composable
fun FWheelPickerFocusVertical(
    modifier: Modifier = Modifier,
    dividerSize: Dp = 1.dp,
    dividerColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(dividerColor)
                .height(dividerSize)
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        )
        Box(
            modifier = Modifier
                .background(dividerColor)
                .height(dividerSize)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        )
    }
}

/**
 * The default implementation of focus view in horizontal.
 */
@Composable
fun FWheelPickerFocusHorizontal(
    modifier: Modifier = Modifier,
    dividerSize: Dp = 1.dp,
    dividerColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(dividerColor)
                .width(dividerSize)
                .fillMaxHeight()
                .align(Alignment.CenterStart),
        )
        Box(
            modifier = Modifier
                .background(dividerColor)
                .width(dividerSize)
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
        )
    }
}

/**
 * Default content wrapper.
 */
val DefaultWheelPickerContentWrapper: @Composable FWheelPickerContentWrapperScope.(index: Int, state: FWheelPickerState) -> Unit = { index, state ->
    val alpha = if (state.currentIndexSnapshot == index) 1.0f else 0.3f
    val animateScale by animateFloatAsState(if (state.currentIndexSnapshot == index) 1.0f else 0.8f)
    Box(
        modifier = Modifier
            .alpha(alpha)
            .scale(animateScale),
    ) {
        content(index)
    }
}