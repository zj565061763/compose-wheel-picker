package com.sd.demo.wheel_picker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.FWheelPickerFocusVertical

class SampleCustomFocus : ComponentActivity() {
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
   Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp)
   ) {
      FVerticalWheelPicker(
         modifier = Modifier.width(64.dp),
         count = 50,
         focus = {
            // Custom divider.
            FWheelPickerFocusVertical(dividerColor = Color.Red, dividerSize = 2.dp)
         },
      ) { index ->
         Text(index.toString())
      }

      FVerticalWheelPicker(
         modifier = Modifier.width(64.dp),
         count = 50,
         focus = {
            // Custom focus.
            Box(
               modifier = Modifier
                  .fillMaxSize()
                  .border(width = 1.dp, color = Color.Gray)
            )
         },
      ) { index ->
         Text(index.toString())
      }
   }
}

@Preview
@Composable
private fun Preview() {
   Content()
}