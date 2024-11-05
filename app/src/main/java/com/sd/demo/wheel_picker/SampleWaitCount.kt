package com.sd.demo.wheel_picker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SampleWaitCount : ComponentActivity() {
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
private fun Content() {
   Column(
      modifier = Modifier
         .fillMaxSize()
         .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
   ) {
      Sample()
   }
}

@Composable
private fun Sample() {
   // Initial index.
   val state = rememberFWheelPickerState()
   var count by remember { mutableIntStateOf(0) }

   LaunchedEffect(state) {
      val job = launch {
         // Because the count is 0, it will suspend here.
         state.scrollToIndex(10)
      }

      delay(1_000)
      // The job will resume after count is not 0.
      count = 50
   }

   FVerticalWheelPicker(
      modifier = Modifier.width(64.dp),
      count = count,
      // state
      state = state,
      debug = true,
   ) { index ->
      Text(index.toString())
   }
}

@Preview
@Composable
private fun Preview() {
   Content()
}