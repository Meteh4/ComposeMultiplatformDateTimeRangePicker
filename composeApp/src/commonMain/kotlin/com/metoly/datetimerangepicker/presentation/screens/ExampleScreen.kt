package com.metoly.datetimerangepicker.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.metoly.datetimerangepicker.model.rememberDateRangePickerState
import com.metoly.datetimerangepicker.presentation.components.DateTimeRangeField
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * This composable function displays a screen demonstrating the use of the DateTimeRangePicker.
 * It showcases both standard date range selection and single date selection modes.
 * The screen includes two cards, each containing a DateTimeRangeField for user interaction.
 * The selected date(s) are displayed below each field.
 */
@Composable
fun ExampleScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "DateTime Range Picker Demo",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val standardState = rememberDateRangePickerState()
                    var standardSelectedRange by remember { mutableStateOf<Pair<LocalDateTime?, LocalDateTime?>>(null to null) }

                    Text(
                        text = "Standard Date Range Selection",
                        style = MaterialTheme.typography.titleMedium
                    )

                    DateTimeRangeField(
                        state = standardState,
                        onRangeSelected = {
                            standardSelectedRange = it
                        }
                    )

                    if (standardSelectedRange.first != null) {
                        Text(
                            text = "Selected range: ${formatSelectedRange(standardSelectedRange)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val singleState = rememberDateRangePickerState(singleSelection = true)
                    var singleSelectedDate by remember { mutableStateOf<LocalDateTime?>(null) }

                    Text(
                        text = "Single Date Selection",
                        style = MaterialTheme.typography.titleMedium
                    )

                    DateTimeRangeField(
                        state = singleState,
                        onRangeSelected = {
                            singleSelectedDate = it.first
                        }
                    )

                    if (singleSelectedDate != null) {
                        Text(
                            text = "Selected date: ${formatDateTime(singleSelectedDate!!)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExampleScreenPreview() {
    ExampleScreen()
}

private fun formatSelectedRange(range: Pair<LocalDateTime?, LocalDateTime?>): String {
    return when {
        range.first != null && range.second != null ->
            "${formatDateTime(range.first!!)} - ${formatDateTime(range.second!!)}"
        range.first != null ->
            formatDateTime(range.first!!)
        else ->
            "No selection"
    }
}

private fun formatDateTime(dateTime: LocalDateTime): String {
    val day = dateTime.date.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.date.monthNumber.toString().padStart(2, '0')
    val year = dateTime.date.year.toString()
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')

    return "$day.$month.$year $hour:$minute"
}