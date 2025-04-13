package com.metoly.datetimerangepicker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.metoly.datetimerangepicker.model.NumberPickerItem
import com.metoly.datetimerangepicker.model.VerticalWheelPicker
import com.metoly.datetimerangepicker.model.rememberWheelPickerState
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A composable function that displays a time picker.
 *
 * @param initialDateTime The initial date and time to display in the picker.
 * @param onDone A callback function that is invoked when the user presses the "Done" button.
 *               It receives the selected date and time as a [LocalDateTime] object.
 */
@Composable
fun TimePicker(
    initialDateTime: LocalDateTime,
    onDone: (LocalDateTime) -> Unit
) {
    val hourState = rememberWheelPickerState(initialIndex = initialDateTime.hour)
    val minuteState = rememberWheelPickerState(initialIndex = initialDateTime.minute)

    val isScrolling = remember { mutableStateOf(false) }

    LaunchedEffect(hourState.lazyListState.isScrollInProgress, minuteState.lazyListState.isScrollInProgress) {
        isScrolling.value = hourState.lazyListState.isScrollInProgress ||
                minuteState.lazyListState.isScrollInProgress
    }

    val itemHeight = 60.dp

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Time",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            VerticalWheelPicker(
                modifier = Modifier.weight(1f),
                count = 24,
                state = hourState,
                itemHeight = itemHeight,
                unfocusedCount = 1
            ) { index ->
                NumberPickerItem(
                    number = index,
                    isSelected = index == hourState.currentIndexSnapshot
                )
            }

            Text(
                text = ":",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            VerticalWheelPicker(
                modifier = Modifier.weight(1f),
                count = 60,
                state = minuteState,
                itemHeight = itemHeight,
                unfocusedCount = 1
            ) { index ->
                NumberPickerItem(
                    number = index,
                    isSelected = index == minuteState.currentIndexSnapshot
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val newDateTime = LocalDateTime(
                    initialDateTime.date,
                    LocalTime(hourState.currentIndexSnapshot, minuteState.currentIndexSnapshot)
                )
                onDone(newDateTime)
            },
            enabled = !isScrolling.value
        ) {
            Text("Done")
        }
    }
}

@Preview
@Composable
fun TimePickerPreview() {
    val initialDateTime = LocalDateTime(2024, 1, 1, 12, 30)
    TimePicker(
        initialDateTime = initialDateTime,
        onDone = { }
    )
}