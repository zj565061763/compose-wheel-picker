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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import kotlinx.coroutines.delay

class SampleScrollToIndex : ComponentActivity() {
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
   val state = rememberFWheelPickerState(10)

   LaunchedEffect(state) {
      delay(3_000)
      // Scroll to index.
      state.animateScrollToIndex(20)
   }

   FVerticalWheelPicker(
      modifier = Modifier.width(64.dp),
      count = 50,
      // state
      state = state,
   ) { index ->
      Text(index.toString())
   }
}

@Preview
@Composable
private fun Preview() {
   Content()
}