# About

Android Compose wheel picker library based on LazyColumn in vertical and LazyRow in horizontal.

# Gradle

[![](https://jitpack.io/v/zj565061763/compose-wheel-picker.svg)](https://jitpack.io/#zj565061763/compose-wheel-picker)

# Sample

| Default | Item size | Unfocused count | Custom divider | Custom focus |
| :----: | :----: | :----: | :----: | :----: |
| <img src="https://thumbsnap.com/i/9MTLo4FX.gif?0714" width="100px"/> | <img src="https://thumbsnap.com/i/18SBUBHg.gif?0714" width="100px"/> | <img src="https://thumbsnap.com/i/qH5Z6wL8.gif?0714" width="100px"/> | <img src="https://thumbsnap.com/i/EyjJoDB9.gif?0714" width="150px"/> | <img src="https://thumbsnap.com/i/DhyaDVkH.gif?0714" width="150px"/> |

| Scroll to index | Observe index | Custom display | Reverse layout | Horizontal |
| :----: | :----: | :----: | :----: | :----: |
| <img src="https://thumbsnap.com/i/5juVMWPU.gif?0714" width="100px"/> | <img src="https://thumbsnap.com/i/6rHShNK4.gif?0714" width="200px"/> | <img src="https://thumbsnap.com/i/cLwTSLZC.gif?0714" width="100px"/> | <img src="https://thumbsnap.com/i/TMtF439g.gif?0714" width="100px"/> | <img src="https://thumbsnap.com/i/enX2Prc8.gif?0714" width="100px"/> |

### Default

```kotlin
FVerticalWheelPicker(
    modifier = Modifier.width(60.dp),
    // Specified item count.
    count = 50,
) { index ->
    Text(index.toString())
}
```

### Item size

```kotlin
FVerticalWheelPicker(
    // ......
    // Specified item height.
    itemHeight = 60.dp,
) {
    // ......
}
```

### Unfocused count

```kotlin
FVerticalWheelPicker(
    // ......
    // Specified unfocused count.
    unfocusedCount = 2,
) {
    // ......
}
```

### Custom divider

```kotlin
FVerticalWheelPicker(
    // ......
    focus = {
        // Custom divider.
        FWheelPickerFocusVertical(dividerColor = Color.Red, dividerSize = 2.dp)
    },
) {
    // ......
}
```

### Custom focus

```kotlin
FVerticalWheelPicker(
    // ......
    // Custom focus.
    focus = {
        Box(modifier = Modifier
            .fillMaxSize()
            .border(width = 1.dp, color = Color.Gray))
    },
) {
    // ......
}
```

### Scroll to index

```kotlin
// Specified initial index.
val state = rememberFWheelPickerState(10)
LaunchedEffect(state) {
    delay(2000)
    // Scroll to index.
    state.animateScrollToIndex(20)
}

FVerticalWheelPicker(
    // ......
    // state
    state = state,
) {
    // ......
}
```

### Observe index

* `FWheelPickerState.currentIndex` - Index of picker when it is idle.
* `FWheelPickerState.currentIndexSnapshot` - Index of picker when it is idle or scrolling but not fling.

```kotlin
val state = rememberFWheelPickerState()
FVerticalWheelPicker(
    // ......
    // state
    state = state,
) {
    // ......
}

// Observe currentIndex.
LaunchedEffect(state) {
    snapshotFlow { state.currentIndex }
        .collect {
            Log.i(TAG, "currentIndex ${state.currentIndex}")
        }
}

// Observe currentIndexSnapshot.
LaunchedEffect(state) {
    snapshotFlow { state.currentIndexSnapshot }
        .collect {
            Log.i(TAG, "currentIndexSnapshot ${state.currentIndexSnapshot}")
        }
}
```

### Custom display

```kotlin
FVerticalWheelPicker(
    // ......
    // Content display
    display = { index ->
        if (state.currentIndexSnapshot == index) {
            content(index)
        } else {
            // Modify content if it is not in focus.
            Box(
                modifier = Modifier
                    .rotate(90f)
                    .alpha(0.5f)
            ) {
                content(index)
            }
        }
    }
) {
    // ......
}
```

### Reverse layout

```kotlin
FVerticalWheelPicker(
    // ......
    // Reverse layout.
    reverseLayout = true,
) {
    // ......
}
```

### Horizontal

`FHorizontalWheelPicker` is almost the same as `FVerticalWheelPicker`.

```kotlin
FHorizontalWheelPicker(
    modifier = Modifier.height(60.dp),
    // Specified item count.
    count = 50,
) { index ->
    Text(index.toString())
}
```
