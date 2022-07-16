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
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
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
    private var _currentIndexSnapshot by mutableStateOf(-1)

    private var _pendingIndex: Int? = null
        set(value) {
            field = value
            if (value == null) resumeAwaitScroll()
        }
    private var _pendingIndexContinuation: Continuation<Unit>? = null

    /**
     * Item count
     */
    private var _count = 0
        set(value) {
            field = value.coerceAtLeast(0)
        }

    /**
     * The item index closest to the viewport start.
     */
    private val _mostStartItemIndex: Int
        get() = (_mostStartItemInfo?.index ?: -1).also {
            _currentIndexSnapshot = it
        }

    /**
     * The item closest to the viewport start.
     */
    private val _mostStartItemInfo: LazyListItemInfo?
        get() {
            if (_count <= 0) return null
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

    /**
     * Index of picker when it is idle.
     *
     * Note that this property is observable and if you use it in the composable function
     * it will be recomposed on every change.
     */
    @get:IntRange(from = -1)
    val currentIndex: Int
        get() = _currentIndex

    /**
     * Index of picker when it is idle or scrolling but not fling.
     *
     * Note that this property is observable and if you use it in the composable function
     * it will be recomposed on every change.
     */
    @get:IntRange(from = -1)
    val currentIndexSnapshot: Int
        get() = _currentIndexSnapshot

    val hasPendingScroll: Boolean
        get() = _pendingIndex != null

    suspend fun animateScrollToIndex(
        @IntRange(from = 0) index: Int,
    ) {
        lazyListState.animateScrollToItem(index.coerceAtLeast(0))
        synchronizeCurrentIndex()
    }

    suspend fun scrollToIndex(
        @IntRange(from = 0) index: Int,
        pending: Boolean = true,
    ) {
        val safeIndex = index.coerceAtLeast(0)
        lazyListState.scrollToItem(safeIndex)
        synchronizeCurrentIndex()

        if (pending) {
            if (_currentIndex != safeIndex) {
                awaitScroll(safeIndex)
            }
        }

        if (_currentIndex == safeIndex) {
            _pendingIndex = null
        }
    }

    private suspend fun awaitScroll(index: Int) {
        logMsg { "awaitScroll index $index start" }

        // Resume last continuation before suspend.
        resumeAwaitScroll()

        _pendingIndex = index
        suspendCoroutine { _pendingIndexContinuation = it }
        logMsg { "awaitScroll index $index finish" }
    }

    private fun resumeAwaitScroll() {
        _pendingIndexContinuation?.let {
            logMsg { "resumeAwaitScroll" }
            it.resume(Unit)
            _pendingIndexContinuation = null
        }
    }

    internal suspend fun notifyCountChanged(count: Int) {
        logMsg { "notifyCountChanged count:$count currentIndex:$_currentIndex pendingIndex:$_pendingIndex" }
        _count = count
        val maxIndex = count - 1
        if (_currentIndex > maxIndex) {
            updateCurrentIndexInternal(maxIndex)
        } else {
            _pendingIndex?.let { pendingIndex ->
                if (count > pendingIndex) {
                    scrollToIndex(pendingIndex, pending = false)
                }
            }
            if (_currentIndex < 0) {
                synchronizeCurrentIndex()
            }
        }
    }

    private fun synchronizeCurrentIndex() {
        updateCurrentIndexInternal(_mostStartItemIndex)
    }

    private fun updateCurrentIndexInternal(index: Int) {
        val safeIndex = index.coerceAtLeast(-1)
        if (_currentIndex != safeIndex) {
            logMsg { "Current index changed $safeIndex" }
            _currentIndex = safeIndex
            _currentIndexSnapshot = safeIndex
            if (_pendingIndex == safeIndex) {
                _pendingIndex = null
            }
        }
    }

    internal fun synchronizeCurrentIndexSnapshot(): Int {
        return _mostStartItemIndex
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