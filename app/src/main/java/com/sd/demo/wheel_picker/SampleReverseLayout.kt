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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker

class SampleReverseLayout : ComponentActivity() {
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
      // Reverse layout.
      reverseLayout = true,
   ) {
      Text(it.toString())
   }
}

@Preview
@Composable
private fun Preview() {
   Content()
}