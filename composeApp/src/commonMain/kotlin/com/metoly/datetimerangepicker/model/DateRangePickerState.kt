package com.metoly.datetimerangepicker.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface DateSelectionHandler {
    fun handleSelection(
        selectedDateTime: LocalDateTime,
        currentStart: LocalDateTime?,
        currentEnd: LocalDateTime?
    ): Pair<LocalDateTime?, LocalDateTime?>
}

class SingleDateHandler : DateSelectionHandler {
    override fun handleSelection(
        selectedDateTime: LocalDateTime,
        currentStart: LocalDateTime?,
        currentEnd: LocalDateTime?
    ) =
        selectedDateTime to null
}

class RangeDateHandler : DateSelectionHandler {
    override fun handleSelection(
        selectedDateTime: LocalDateTime,
        currentStart: LocalDateTime?,
        currentEnd: LocalDateTime?
    ): Pair<LocalDateTime?, LocalDateTime?> {
        return when {
            currentStart == null -> selectedDateTime to null
            currentEnd == null -> handleSecondSelection(selectedDateTime, currentStart)
            else -> handleExistingRangeSelection(selectedDateTime, currentStart, currentEnd)
        }
    }

    private fun handleSecondSelection(
        selectedDateTime: LocalDateTime,
        start: LocalDateTime
    ) = if (selectedDateTime < start) {
        selectedDateTime to start
    } else {
        start to selectedDateTime
    }

    private fun handleExistingRangeSelection(
        selectedDateTime: LocalDateTime,
        currentStart: LocalDateTime,
        currentEnd: LocalDateTime
    ): Pair<LocalDateTime?, LocalDateTime?> {
        val selectedDate = selectedDateTime.date
        val startDate = currentStart.date
        val endDate = currentEnd.date

        return when {
            selectedDate == startDate -> selectedDateTime to currentEnd
            selectedDate == endDate -> currentStart to selectedDateTime
            selectedDateTime < currentStart -> selectedDateTime to currentEnd
            selectedDateTime > currentEnd -> currentStart to selectedDateTime
            else -> currentStart to selectedDateTime
        }
    }
}

class DateRangePickerState(
    initialYear: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    initialMonth: Month = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month,
    val singleSelection: Boolean = false
) {
    private val dateSelectionHandler: DateSelectionHandler =
        if (singleSelection) SingleDateHandler() else RangeDateHandler()

    private var _currentYear by mutableStateOf(initialYear)
    private var _currentMonth by mutableStateOf(initialMonth)
    private var _selectedRange by mutableStateOf<Pair<LocalDateTime?, LocalDateTime?>>(null to null)

    val currentYear: Int get() = _currentYear
    val currentMonth: Month get() = _currentMonth
    val selectedRange: Pair<LocalDateTime?, LocalDateTime?> get() = _selectedRange

    val formattedDates: String?
        get() = _selectedRange.let { (start, end) ->
            when {
                start != null && end != null -> "${formatDateTime(start)} - ${formatDateTime(end)}"
                start != null -> formatDateTime(start)
                else -> null
            }
        }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        val day = dateTime.date.dayOfMonth.toString().padStart(2, '0')
        val month = dateTime.date.monthNumber.toString().padStart(2, '0')
        val year = dateTime.date.year.toString().takeLast(2)
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')

        return "$day-$month-$year $hour:$minute"
    }

    fun selectDateTime(dateTime: LocalDateTime) {
        _selectedRange = dateSelectionHandler.handleSelection(
            dateTime,
            _selectedRange.first,
            _selectedRange.second
        )
    }

    fun navigateToPreviousMonth() {
        if (_currentMonth == Month.JANUARY) {
            _currentMonth = Month.DECEMBER
            _currentYear--
        } else {
            _currentMonth = Month.values()[_currentMonth.ordinal - 1]
        }
    }

    fun navigateToNextMonth() {
        if (_currentMonth == Month.DECEMBER) {
            _currentMonth = Month.JANUARY
            _currentYear++
        } else {
            _currentMonth = Month.values()[_currentMonth.ordinal + 1]
        }
    }

    fun resetSelection() {
        _selectedRange = null to null
    }
}

@Composable
fun rememberDateRangePickerState(
    initialYear: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    initialMonth: Month = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month,
    singleSelection: Boolean = false
): DateRangePickerState {
    return remember {
        DateRangePickerState(
            initialYear = initialYear,
            initialMonth = initialMonth,
            singleSelection = singleSelection
        )
    }
}