package com.sd.demo.wheel_picker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker

class SampleCustomDisplay : ComponentActivity() {
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
   FVerticalWheelPicker(
      modifier = Modifier.width(64.dp),
      count = 50,
      display = { index ->
         if (state.currentIndexSnapshot == index) {
            Content(index)
         } else {
            // Modify content if it is not in focus.
            Box(
               modifier = Modifier
                  .rotate(90f)
                  .alpha(0.5f)
            ) {
               Content(index)
            }
         }
      }
   ) { index ->
      Text(index.toString())
   }
}

@Preview
@Composable
private fun Preview() {
   Content()
}