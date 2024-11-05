package com.sd.demo.wheel_picker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import com.sd.lib.date.FDate
import com.sd.lib.date.FDateSelector
import com.sd.lib.date.fCurrentDate
import com.sd.lib.date.fDate
import com.sd.lib.date.selectDayOfMonthWithIndex
import com.sd.lib.date.selectMonthWithIndex
import com.sd.lib.date.selectYearWithIndex

class SampleDatePicker : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         AppTheme {
            Content()
         }
      }
   }
}

@Composable
private fun Content(
   modifier: Modifier = Modifier,
) {
   var selectDate by remember { mutableStateOf(fCurrentDate()) }
   var showPicker by remember { mutableStateOf(false) }

   Box(modifier = modifier.fillMaxSize()) {
      TextButton(
         modifier = Modifier.align(Alignment.TopCenter),
         onClick = { showPicker = true }
      ) {
         Text(text = selectDate.toString())
      }

      Picker(
         modifier = Modifier.align(Alignment.Center),
         show = showPicker,
         date = selectDate,
         onDone = { date ->
            showPicker = false
            if (date != null) {
               selectDate = date
            }
         },
      )
   }
}

@Composable
private fun Picker(
   modifier: Modifier = Modifier,
   show: Boolean,
   date: FDate,
   onDone: (FDate?) -> Unit,
) {
   val selector = remember {
      FDateSelector(
         startDate = fDate(2000, 1, 1),
         endDate = fCurrentDate(),
      )
   }

   LaunchedEffect(selector, date) {
      selector.setDate(date)
   }

   val state by selector.stateFlow.collectAsStateWithLifecycle()

   if (show) {
      Column(modifier = modifier.fillMaxWidth()) {
         PickDateView(
            listYear = state.listYear,
            listMonth = state.listMonth,
            listDayOfMonth = state.listDayOfMonth,
            indexOfYear = state.indexOfYear,
            indexOfMonth = state.indexOfMonth,
            indexOfDayOfMonth = state.indexOfDayOfMonth,
            onYearIndexChange = {
               selector.selectYearWithIndex(it)
            },
            onMonthIndexChange = {
               selector.selectMonthWithIndex(it)
            },
            onDayOfMonthIndexChange = {
               selector.selectDayOfMonthWithIndex(it)
            },
         )

         Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { onDone(selector.date) },
         ) {
            Text(text = "Done")
         }
      }
   }
}

@Composable
private fun PickDateView(
   modifier: Modifier = Modifier,
   listYear: List<Int>,
   listMonth: List<Int>,
   listDayOfMonth: List<Int>,
   indexOfYear: Int,
   indexOfMonth: Int,
   indexOfDayOfMonth: Int,
   onYearIndexChange: suspend (Int) -> Unit,
   onMonthIndexChange: suspend (Int) -> Unit,
   onDayOfMonthIndexChange: suspend (Int) -> Unit,
) {
   if (indexOfYear < 0) return
   if (indexOfMonth < 0) return
   if (indexOfDayOfMonth < 0) return

   val yearState = rememberFWheelPickerState(indexOfYear)
   val monthState = rememberFWheelPickerState(indexOfMonth)
   val dayOfMonthState = rememberFWheelPickerState(indexOfDayOfMonth)

   LaunchedEffect(yearState) {
      snapshotFlow { yearState.currentIndex }
         .collect(onYearIndexChange)
   }

   LaunchedEffect(monthState) {
      snapshotFlow { monthState.currentIndex }
         .collect(onMonthIndexChange)
   }

   LaunchedEffect(dayOfMonthState) {
      snapshotFlow { dayOfMonthState.currentIndex }
         .collect(onDayOfMonthIndexChange)
   }

   Row(
      modifier = modifier
         .fillMaxWidth()
         .padding(horizontal = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
   ) {
      // Year
      FVerticalWheelPicker(
         modifier = Modifier.weight(1f),
         state = yearState,
         count = listYear.size,
         unfocusedCount = 2,
      ) { index ->
         listYear.getOrNull(index)?.let { value ->
            Text(text = value.toString())
         }
      }

      // Month
      FVerticalWheelPicker(
         modifier = Modifier.weight(1f),
         state = monthState,
         count = listMonth.size,
         unfocusedCount = 2,
      ) { index ->
         listMonth.getOrNull(index)?.let { value ->
            Text(text = value.toString())
         }
      }

      // Day of month
      FVerticalWheelPicker(
         modifier = Modifier.weight(1f),
         state = dayOfMonthState,
         count = listDayOfMonth.size,
         unfocusedCount = 2,
      ) { index ->
         listDayOfMonth.getOrNull(index)?.let { value ->
            Text(text = value.toString())
         }
      }
   }
}