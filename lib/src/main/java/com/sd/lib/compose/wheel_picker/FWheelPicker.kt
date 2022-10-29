package com.sd.lib.compose.wheel_picker

import android.util.Log
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun FVerticalWheelPicker(
    count: Int,
    state: FWheelPickerState = rememberFWheelPickerState(),
    modifier: Modifier = Modifier,
    key: ((index: Int) -> Any)? = null,
    itemHeight: Dp = 35.dp,
    unfocusedCount: Int = 1,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    focus: @Composable () -> Unit = { FWheelPickerFocusVertical() },
    contentWrapper: @Composable FWheelPickerContentWrapperScope.(index: Int, state: FWheelPickerState) -> Unit = DefaultWheelPickerContentWrapper,
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    WheelPicker(
        isVertical = true,
        count = count,
        state = state,
        modifier = modifier,
        key = key,
        itemSize = itemHeight,
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
    count: Int,
    state: FWheelPickerState = rememberFWheelPickerState(),
    modifier: Modifier = Modifier,
    key: ((index: Int) -> Any)? = null,
    itemWidth: Dp = 35.dp,
    unfocusedCount: Int = 1,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    focus: @Composable () -> Unit = { FWheelPickerFocusHorizontal() },
    contentWrapper: @Composable FWheelPickerContentWrapperScope.(index: Int, state: FWheelPickerState) -> Unit = DefaultWheelPickerContentWrapper,
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    WheelPicker(
        isVertical = false,
        count = count,
        state = state,
        modifier = modifier,
        key = key,
        itemSize = itemWidth,
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
    isVertical: Boolean,
    count: Int,
    state: FWheelPickerState,
    modifier: Modifier,
    key: ((index: Int) -> Any)?,
    itemSize: Dp,
    unfocusedCount: Int,
    userScrollEnabled: Boolean,
    reverseLayout: Boolean,
    focus: @Composable () -> Unit,
    contentWrapper: @Composable FWheelPickerContentWrapperScope.(index: Int, state: FWheelPickerState) -> Unit,
    content: @Composable FWheelPickerContentScope.(index: Int) -> Unit,
) {
    require(count >= 0) { "require count >= 0" }
    require(unfocusedCount >= 1) { "require unfocusedCount >= 1" }

    val densityUpdated by rememberUpdatedState(LocalDensity.current)
    val itemSizeUpdated by rememberUpdatedState(itemSize)
    val unfocusedCountUpdated by rememberUpdatedState(unfocusedCount)
    val reverseLayoutUpdated by rememberUpdatedState(reverseLayout)

    val totalSize by remember {
        derivedStateOf { itemSizeUpdated * (unfocusedCountUpdated * 2 + 1) }
    }

    LaunchedEffect(state, count) {
        state.notifyCountChanged(count)
    }

    val nestedScrollConnection = remember(state) {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                state.synchronizeCurrentIndexSnapshot()
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val currentIndex = state.synchronizeCurrentIndexSnapshot()
                return if (currentIndex >= 0) {
                    val flingItemCount = available.flingItemCount(
                        isVertical = isVertical,
                        itemSize = with(densityUpdated) { itemSizeUpdated.roundToPx() },
                        decay = exponentialDecay(2f),
                        reverseLayout = reverseLayoutUpdated,
                    )

                    if (flingItemCount.absoluteValue > 0) {
                        state.animateScrollToIndex(currentIndex - flingItemCount)
                    } else {
                        state.animateScrollToIndex(currentIndex)
                    }
                    available
                } else {
                    super.onPreFling(available)
                }
            }
        }
    }

    val contentUpdated by rememberUpdatedState(content)
    val contentScope by remember(state) { mutableStateOf(WheelPickerContentScopeImpl(state)) }

    val contentWrapperScope = remember(contentScope) {
        object : FWheelPickerContentWrapperScope {
            @Composable
            override fun content(index: Int) {
                contentUpdated.invoke(contentScope, index)
            }
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .run {
                if (totalSize > 0.dp) {
                    if (isVertical) {
                        height(totalSize).widthIn(40.dp)
                    } else {
                        width(totalSize).heightIn(40.dp)
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
                    contentWrapper.invoke(contentWrapperScope, index, state)
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

private fun Velocity.flingItemCount(
    isVertical: Boolean,
    itemSize: Int,
    decay: DecayAnimationSpec<Float>,
    reverseLayout: Boolean,
): Int {
    if (itemSize <= 0) return 0
    val velocity = if (isVertical) y else x
    val targetValue = decay.calculateTargetValue(0f, velocity)
    val flingItemCount = (targetValue / itemSize).toInt()
    return if (reverseLayout) -flingItemCount else flingItemCount
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