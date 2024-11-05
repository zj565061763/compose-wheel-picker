package com.sd.lib.compose.wheel_picker

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.absoluteValue

@Composable
fun rememberFWheelPickerState(
   initialIndex: Int = 0,
): FWheelPickerState {
   val coroutineScope = rememberCoroutineScope()
   val saveableIndex = rememberSaveable { initialIndex }
   return remember(coroutineScope) {
      FWheelPickerState(
         coroutineScope = coroutineScope,
         initialIndex = saveableIndex,
      )
   }
}

class FWheelPickerState internal constructor(
   coroutineScope: CoroutineScope,
   initialIndex: Int,
) {
   internal var debug = false
   internal val lazyListState = LazyListState()
   internal var isReady by mutableStateOf(false)

   private var _count = 0
   private var _currentIndex by mutableIntStateOf(-1)
   private var _currentIndexSnapshot by mutableIntStateOf(-1)

   private var _pendingIndex: Int? = initialIndex.coerceAtLeast(0)
   private var _pendingIndexContinuation: CancellableContinuation<Unit>? = null
      set(value) {
         field = value
         if (value == null) _pendingIndex = null
      }

   /**
    * Index of picker when it is idle, -1 means that there is no data.
    *
    * Note that this property is observable and if you use it in the composable function
    * it will be recomposed on every change.
    */
   val currentIndex: Int get() = _currentIndex

   /**
    * Index of picker when it is idle or drag but not fling, -1 means that there is no data.
    *
    * Note that this property is observable and if you use it in the composable function
    * it will be recomposed on every change.
    */
   val currentIndexSnapshot: Int get() = _currentIndexSnapshot

   /**
    * [LazyListState.interactionSource]
    */
   val interactionSource: InteractionSource get() = lazyListState.interactionSource

   /**
    * [LazyListState.isScrollInProgress]
    */
   val isScrollInProgress: Boolean get() = lazyListState.isScrollInProgress

   suspend fun animateScrollToIndex(index: Int) {
      logMsg(debug) { "animateScrollToIndex index:$index count:$_count" }
      @Suppress("NAME_SHADOWING")
      val index = index.coerceAtLeast(0)
      lazyListState.animateScrollToItem(index)
      synchronizeCurrentIndex()
   }

   suspend fun scrollToIndex(index: Int) {
      logMsg(debug) { "scrollToIndex index:$index count:$_count" }
      @Suppress("NAME_SHADOWING")
      val index = index.coerceAtLeast(0)

      // Always cancel last continuation.
      _pendingIndexContinuation?.let {
         logMsg(debug) { "cancelAwaitIndex" }
         _pendingIndexContinuation = null
         it.cancel()
      }

      awaitIndex(index)

      lazyListState.scrollToItem(index)
      synchronizeCurrentIndex()
   }

   private suspend fun awaitIndex(index: Int) {
      if (_count > 0) return
      logMsg(debug) { "awaitIndex:$index start" }
      suspendCancellableCoroutine { cont ->
         _pendingIndex = index
         _pendingIndexContinuation = cont
         cont.invokeOnCancellation {
            logMsg(debug) { "awaitIndex:$index canceled" }
            _pendingIndexContinuation = null
         }
      }
      logMsg(debug) { "awaitIndex:$index finish" }
   }

   internal suspend fun updateCount(count: Int) {
      logMsg(debug) { "updateCount count:$count currentIndex:$_currentIndex" }

      // Update count
      _count = count

      val maxIndex = count - 1
      if (maxIndex < _currentIndex) {
         if (count > 0) {
            scrollToIndex(maxIndex)
         } else {
            synchronizeCurrentIndex()
         }
      }

      if (count > 0) {
         val pendingIndex = _pendingIndex
         if (pendingIndex != null) {
            logMsg(debug) { "Found pendingIndex:$pendingIndex pendingIndexContinuation:$_pendingIndexContinuation" }
            val continuation = _pendingIndexContinuation
            _pendingIndexContinuation = null

            if (continuation?.isActive == true) {
               logMsg(debug) { "resume pendingIndexContinuation" }
               continuation.resume(Unit)
            } else {
               scrollToIndex(pendingIndex)
            }
         } else {
            if (_currentIndex < 0) {
               synchronizeCurrentIndex()
            }
         }
      }

      isReady = count > 0
   }

   private fun synchronizeCurrentIndex() {
      val index = synchronizeCurrentIndexSnapshot()
      if (_currentIndex != index) {
         logMsg(debug) { "setCurrentIndex:$index" }
         _currentIndex = index
         _currentIndexSnapshot = index
      }
   }

   internal fun synchronizeCurrentIndexSnapshot(): Int {
      return (mostStartItemInfo()?.index ?: -1).also {
         _currentIndexSnapshot = it
      }
   }

   /**
    * The item closest to the viewport start.
    */
   private fun mostStartItemInfo(): LazyListItemInfo? {
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

   init {
      coroutineScope.launch {
         snapshotFlow { lazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .collect {
               logMsg(debug) { "isScrollInProgress:$it" }
               if (!it) {
                  synchronizeCurrentIndex()
               }
            }
      }
   }
}