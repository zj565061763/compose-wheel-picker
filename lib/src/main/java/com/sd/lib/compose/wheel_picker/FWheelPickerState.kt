package com.sd.lib.compose.wheel_picker

import androidx.annotation.IntRange
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.absoluteValue

@Composable
fun rememberFWheelPickerState(
    @IntRange(from = 0) initialIndex: Int = 0,
): FWheelPickerState = rememberSaveable(saver = FWheelPickerState.Saver) {
    FWheelPickerState(
        initialIndex = initialIndex,
    )
}

class FWheelPickerState(
    @IntRange(from = 0) initialIndex: Int = 0,
) : ScrollableState {
    internal val lazyListState = LazyListState(firstVisibleItemIndex = initialIndex)

    private var _currentIndex by mutableStateOf(-1)

    internal val mostStartItemInfo: LazyListItemInfo?
        get() {
            val layoutInfo = lazyListState.layoutInfo
            val listInfo = layoutInfo.visibleItemsInfo
            if (listInfo.isEmpty()) return null
            if (listInfo.size == 1) return listInfo.first()

            val firstItem = listInfo.first()
            val firstOffsetDelta = (firstItem.offset - layoutInfo.viewportStartOffset).absoluteValue
            return if (firstOffsetDelta < firstItem.size / 2) {
                firstItem
            } else {
                listInfo[1]
            }
        }

    val interactionSource: InteractionSource
        get() = lazyListState.interactionSource

    @get:IntRange(from = -1)
    var currentIndex: Int
        get() = _currentIndex
        internal set(value) {
            val safeValue = value.coerceAtLeast(-1)
            if (_currentIndex != safeValue) {
                _currentIndex = safeValue
                logMsg { "Current index changed:$safeValue" }
            }
        }

    @get:IntRange(from = 0)
    val currentIndexSnapshot: Int
        get() = mostStartItemInfo?.index ?: 0

    suspend fun scrollToIndex(
        @IntRange(from = 0) index: Int,
    ) {
        lazyListState.scrollToItem(index.coerceAtLeast(0))
        updateCurrentIndex()
    }

    suspend fun animateScrollToIndex(
        @IntRange(from = 0) index: Int,
    ) {
        lazyListState.animateScrollToItem(index.coerceAtLeast(0))
        updateCurrentIndex()
    }

    internal fun updateCurrentIndex() {
        currentIndex = currentIndexSnapshot
    }

    override val isScrollInProgress: Boolean
        get() = lazyListState.isScrollInProgress

    override suspend fun scroll(scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit) {
        lazyListState.scroll(scrollPriority, block)
    }

    override fun dispatchRawDelta(delta: Float): Float {
        return lazyListState.dispatchRawDelta(delta)
    }

    companion object {
        val Saver: Saver<FWheelPickerState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.currentIndex,
                )
            },
            restore = {
                FWheelPickerState(
                    initialIndex = it[0] as Int,
                )
            }
        )
    }
}