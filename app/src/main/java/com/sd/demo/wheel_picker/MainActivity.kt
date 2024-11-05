package com.sd.demo.wheel_picker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sd.demo.wheel_picker.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         AppTheme {
            Content(
               listActivity = listOf(
                  SampleDefault::class.java,
                  SampleCustomFocus::class.java,
                  SampleCustomDisplay::class.java,
                  SampleObserveIndex::class.java,
                  SampleScrollToIndex::class.java,
                  SampleWaitCount::class.java,
                  SampleReverseLayout::class.java,
               ),
               onClickActivity = {
                  startActivity(Intent(this, it))
               },
            )
         }
      }
   }
}

@Composable
private fun Content(
   listActivity: List<Class<out Activity>>,
   onClickActivity: (Class<out Activity>) -> Unit,
) {
   LazyColumn(
      modifier = Modifier
         .fillMaxSize()
         .statusBarsPadding(),
      verticalArrangement = Arrangement.spacedBy(5.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
   ) {
      items(listActivity) { item ->
         Button(
            onClick = { onClickActivity(item) }
         ) {
            Text(text = item.simpleName)
         }
      }
   }
}

fun logMsg(block: () -> String) {
   Log.i("FWheelPicker-demo", block())
}