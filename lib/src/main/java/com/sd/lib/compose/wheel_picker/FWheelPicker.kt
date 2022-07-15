package com.sd.lib.compose.wheel_picker

import android.util.Log
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun FVerticalWheelPicker(
    modifier: Modifier = Modifier,
    count: Int,
    state: FWheelPickerState = rememberFWheelPickerState(),
    key: ((index: Int) -> Any)? = null,
    itemHeight: Dp = 35.dp,
    unfocusedCount: Int = 1,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    focus: @Composable () -> Unit = {
        FWheelPickerFocusVertical()
    },
    contentWrapper: @Composable FWheelPickerContentWrapperScope.(index: Int, state: FWheelPickerState) -> Unit = DefaultWheelPickerContentWrapper,
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        count = count,
        state = state,
        key = key,
        itemSize = itemHeight,
        isVertical = true,
        unfocusedCount = unfocusedCount,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        focus = focus,
        contentWrapper = contentWrapper,
        content = content,
    )
}

@Composable
fun FHorizontalWheelPicker(
    modifier: Modifier = Modifier,
    count: Int,
    state: FWheelPickerState = rememberFWheelPickerState(),
    key: ((index: Int) -> Any)? = null,
    itemWidth: Dp = 35.dp,
    unfocusedCount: Int = 1,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    focus: @Composable () -> Unit = {
        FWheelPickerFocusHorizontal()
    },
    contentWrapper: @Composable FWheelPickerContentWrapperScope.(index: Int, state: FWheelPickerState) -> Unit = DefaultWheelPickerContentWrapper,
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        count = count,
        state = state,
        key = key,
        itemSize = itemWidth,
        isVertical = false,
        unfocusedCount = unfocusedCount,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        focus = focus,
        contentWrapper = contentWrapper,
        content = content,
    )
}

@Composable
private fun WheelPicker(
    modifier: Modifier,
    count: Int,
    state: FWheelPickerState,
    key: ((index: Int) -> Any)?,
    itemSize: Dp,
    isVertical: Boolean,
    unfocusedCount: Int,
    userScrollEnabled: Boolean,
    reverseLayout: Boolean,
    focus: @Composable () -> Unit,
    contentWrapper: @Composable FWheelPickerContentWrapperScope.(index: Int, state: FWheelPickerState) -> Unit,
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    require(count >= 0) { "count must be >= 0" }
    require(unfocusedCount >= 1) { "unfocusedCount must be >= 1" }

    val stateUpdate by rememberUpdatedState(state)
    LaunchedEffect(count, state) {
        val maxIndex = (count - 1).coerceAtLeast(0)
        if (state.currentIndex > maxIndex) {
            state.currentIndex = maxIndex
        }
    }

    val density = LocalDensity.current

    val itemSizePx = remember(density, itemSize) { with(density) { itemSize.roundToPx() } }
    val totalSizeDp by derivedStateOf {
        val totalSize = (unfocusedCount * 2 + 1) * itemSizePx
        with(density) { totalSize.toDp() }
    }

    val decay = remember(density) { splineBasedDecay<Float>(density) }
    val reverseLayoutUpdate by rememberUpdatedState(reverseLayout)

    val nestedScrollConnection = remember(isVertical, density) {
        object : NestedScrollConnection {
            override suspend fun onPreFling(available: Velocity): Velocity {
                val state = stateUpdate
                val itemInfo = state.mostStartItemInfo
                if (itemInfo != null) {
                    var flingItemCount = available
                        .percentVelocity(
                            isVertical = isVertical,
                            percent = 0.4f,
                        )
                        .flingItemCount(
                            isVertical = isVertical,
                            itemSize = itemSizePx,
                            decay = decay,
                        )

                    if (flingItemCount.absoluteValue > 0) {
                        if (reverseLayoutUpdate) flingItemCount = -flingItemCount
                        val finalIndex = (itemInfo.index - flingItemCount)
                        state.animateScrollToIndex(finalIndex)
                    } else {
                        state.animateScrollToIndex(itemInfo.index)
                    }
                    return available
                }
                return super.onPreFling(available)
            }
        }
    }

    val contentScope by remember(state) { mutableStateOf(WheelPickerContentScopeImpl(state)) }
    val contentUpdate by rememberUpdatedState(content)
    val contentWrapperScope = remember(contentScope) {
        object : FWheelPickerContentWrapperScope {
            @Composable
            override fun content(index: Int) {
                contentScope.contentUpdate(index)
            }
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .run {
                if (totalSizeDp > 0.dp) {
                    if (isVertical) {
                        height(totalSizeDp).widthIn(40.dp)
                    } else {
                        width(totalSizeDp).heightIn(40.dp)
                    }
                } else {
                    this
                }
            },
        contentAlignment = Alignment.Center,
    ) {

        val lazyListScope: LazyListScope.() -> Unit = {
            repeat(unfocusedCount) {
                item {
                    ItemSizeBox(
                        isVertical = isVertical,
                        itemSize = itemSize,
                    )
                }
            }

            items(
                count = count,
                key = key,
            ) { index ->
                ItemSizeBox(
                    isVertical = isVertical,
                    itemSize = itemSize,
                ) {
                    contentWrapperScope.contentWrapper(index, stateUpdate)
                }
            }

            repeat(unfocusedCount) {
                item {
                    ItemSizeBox(
                        isVertical = isVertical,
                        itemSize = itemSize,
                    )
                }
            }
        }

        if (isVertical) {
            LazyColumn(
                state = state.lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListScope,
            )
        } else {
            LazyRow(
                state = state.lazyListState,
                verticalAlignment = Alignment.CenterVertically,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListScope,
            )
        }

        ItemSizeBox(
            modifier = Modifier.align(Alignment.Center),
            isVertical = isVertical,
            itemSize = itemSize,
        ) {
            focus()
        }
    }
}

@Composable
private fun ItemSizeBox(
    modifier: Modifier = Modifier,
    isVertical: Boolean,
    itemSize: Dp,
    content: @Composable () -> Unit = { },
) {
    Box(
        modifier
            .run {
                if (isVertical) {
                    height(itemSize)
                } else {
                    width(itemSize)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private fun Velocity.percentVelocity(
    isVertical: Boolean,
    percent: Float,
): Velocity {
    require(percent > 0 && percent <= 1f)
    return if (isVertical) {
        copy(y = (y * percent))
    } else {
        copy(x = (x * percent))
    }
}

private fun Velocity.flingItemCount(
    isVertical: Boolean,
    itemSize: Int,
    decay: DecayAnimationSpec<Float>,
): Int {
    if (itemSize <= 0) return 0
    val velocity = if (isVertical) y else x
    val targetValue = decay.calculateTargetValue(0f, velocity)
    return (targetValue / itemSize).toInt()
}

interface FWheelPickerContentScope {
    val state: FWheelPickerState
}

private class WheelPickerContentScopeImpl(
    override val state: FWheelPickerState,
) : FWheelPickerContentScope


interface FWheelPickerContentWrapperScope {
    @Composable
    fun content(index: Int)
}

internal inline fun logMsg(block: () -> String) {
    Log.i("FWheelPicker", block())
}