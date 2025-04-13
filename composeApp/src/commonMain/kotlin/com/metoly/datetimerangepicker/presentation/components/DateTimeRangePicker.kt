package com.metoly.datetimerangepicker.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metoly.datetimerangepicker.model.DateRangePickerState
import com.metoly.datetimerangepicker.model.rememberDateRangePickerState
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A composable function that displays a date and time range picker field.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The state of the date range picker. Defaults to a remembered [DateRangePickerState].
 * @param onRangeSelected A callback function that is invoked when a date and time range is selected.
 *   It receives a [Pair] of nullable [LocalDateTime] representing the start and end of the selected range.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeRangeField(
    modifier: Modifier = Modifier,
    state: DateRangePickerState = rememberDateRangePickerState(),
    onRangeSelected: (Pair<LocalDateTime?, LocalDateTime?>) -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf<LocalDateTime?>(null) }

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = {
                showDialog = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column {
                    DateTimePicker(
                        state = state,
                        setTimePickerDate = { showTimePicker = it }
                    )

                    Divider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                state.resetSelection()
                                showDialog = false
                            }
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            enabled = state.selectedRange.first != null &&
                                    (state.singleSelection || state.selectedRange.second != null),
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                onRangeSelected(state.selectedRange)
                                showDialog = false
                            }
                        ) {
                            Text("Apply")
                        }
                    }
                }
            }
        }
    }

    showTimePicker?.let {
        BasicAlertDialog(
            onDismissRequest = {
                showTimePicker = null
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                TimePicker(
                    initialDateTime = it,
                    onDone = { newDateTime ->
                        state.selectDateTime(newDateTime)
                        showTimePicker = null
                    }
                )
            }
        }
    }

    // Animasyonlu input kutucuğu için borderColor animasyonu
    val borderColor by animateColorAsState(
        targetValue = if (state.selectedRange.first != null)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.outline,
        animationSpec = tween(durationMillis = 300),
        label = "borderAnimation"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { showDialog = true }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animasyonlu ikon rengi
            val iconColor by animateColorAsState(
                targetValue = if (state.selectedRange.first != null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 300),
                label = "iconColorAnimation"
            )

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Calendar",
                tint = iconColor
            )

            val hasFormattedDates by remember { derivedStateOf { !state.formattedDates.isNullOrEmpty() } }

            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = hasFormattedDates,
                label = "FormattedDates Animation",
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }
            ) { hasDates ->
                if (hasDates) {
                    Column {
                        Text(
                            text = "Date range",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Text(
                            modifier = Modifier.basicMarquee(),
                            text = state.formattedDates ?: ""
                        )
                    }
                } else {
                    Text(
                        text = "Select date range",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = hasFormattedDates,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                IconButton(
                    onClick = {
                        state.resetSelection()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear selection",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    state: DateRangePickerState = rememberDateRangePickerState(),
    setTimePickerDate: (LocalDateTime) -> Unit = {}
) {
    val daysInMonth = getMonthLength(state.currentYear, state.currentMonth)
    val firstDayOfMonth = getFirstDayOfMonth(state.currentYear, state.currentMonth)

    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.isoDayNumber - 1) % 7

    val (prevYear, prevMonth) = if (state.currentMonth == Month.JANUARY) {
        (state.currentYear - 1) to Month.DECEMBER
    } else {
        state.currentYear to Month.values()[state.currentMonth.ordinal - 1]
    }
    val prevMonthLength = getMonthLength(prevYear, prevMonth)

    val days = mutableListOf<Triple<LocalDate, Boolean, Boolean>>()

    for (i in firstDayOfWeek downTo 1) {
        val day = prevMonthLength - i + 1
        val date = LocalDate(prevYear, prevMonth, day)
        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
        days.add(Triple(date, false, isWeekend))
    }
    for (day in 1..daysInMonth) {
        val date = LocalDate(state.currentYear, state.currentMonth, day)
        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
        days.add(Triple(date, true, isWeekend))
    }

    val (nextYear, nextMonth) = if (state.currentMonth == Month.DECEMBER) {
        (state.currentYear + 1) to Month.JANUARY
    } else {
        state.currentYear to Month.entries.toTypedArray()[state.currentMonth.ordinal + 1]
    }

    val remainingDays = 42 - days.size
    for (day in 1..remainingDays) {
        val date = LocalDate(nextYear, nextMonth, day)
        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
        days.add(Triple(date, false, isWeekend))
    }

    val isHourSelectionVisible = when (state.singleSelection) {
        true -> { state.selectedRange.first != null }
        false -> {state.selectedRange.first != null && state.selectedRange.second != null}
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        AnimatedContent(
            targetState = Pair(state.currentMonth, state.currentYear),
            label = "MonthYearAnimation",
            transitionSpec = {
                slideInHorizontally { width -> width } togetherWith
                        slideOutHorizontally { width -> -width }
            }
        ) { (month, year) ->
            CalendarHeader(state)
        }

        DayOfWeekRow()

        days.chunked(7).forEachIndexed { weekIndex, week ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                week.forEachIndexed { index, (date, isCurrentMonth, isWeekend) ->
                    DateCell(
                        date = date,
                        isCurrentMonth = isCurrentMonth,
                        isWeekend = isWeekend,
                        state = state,
                        isFirstInRow = index == 0,
                        isLastInRow = index == 6
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isHourSelectionVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                state.selectedRange.first?.let { startDate ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formatDate(startDate.date)
                        )
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            onClick = {
                                setTimePickerDate(startDate)
                            }
                        ) {
                            Text(
                                text = formatTime(startDate)
                            )
                        }
                    }
                }

                state.selectedRange.second?.let { endDate ->
                    Text("-")

                    AnimatedVisibility(
                        visible = true,
                        enter = expandHorizontally() + fadeIn(),
                        exit = shrinkHorizontally() + fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = formatDate(endDate.date)
                            )
                            TextButton(
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                onClick = {
                                    setTimePickerDate(endDate)
                                }
                            ) {
                                Text(
                                    text = formatTime(endDate)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(state: DateRangePickerState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                state.navigateToPreviousMonth()
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous month"
            )
        }

        Text(
            text = "${state.currentMonth.name.lowercase().capitalize()} ${state.currentYear}",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        IconButton(
            onClick = {
                state.navigateToNextMonth()
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next month"
            )
        }
    }
}

@Composable
private fun DayOfWeekRow() {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Row(modifier = Modifier.fillMaxWidth()) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RowScope.DateCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isWeekend: Boolean,
    state: DateRangePickerState,
    isFirstInRow: Boolean,
    isLastInRow: Boolean
) {
    val isSelectedStartDate = state.selectedRange.first?.date == date
    val isSelectedEndDate = state.selectedRange.second?.date == date
    val isSelected = isSelectedStartDate || isSelectedEndDate

    val isInRange = if (state.selectedRange.first != null && state.selectedRange.second != null) {
        val start = state.selectedRange.first!!.date
        val end = state.selectedRange.second!!.date
        date > start && date < end
    } else {
        false
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        isWeekend -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isInRange -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else -> Color.White
        },
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "backgroundColorAnimation"
    )

    val shape = when {
        isSelectedStartDate && (isLastInRow || state.selectedRange.second == null) ->
            RoundedCornerShape(24.dp)
        isSelectedStartDate ->
            RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
        isSelectedEndDate && (isFirstInRow || state.selectedRange.first == null) ->
            RoundedCornerShape(24.dp)
        isSelectedEndDate ->
            RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
        isInRange && isFirstInRow ->
            RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
        isInRange && isLastInRow ->
            RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
        else ->
            RectangleShape
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .clip(shape = shape)
            .background(color = backgroundColor, shape = shape)
            .clickable {
                val selectedDateTime = LocalDateTime(
                    date,
                    LocalTime(0, 0)
                )

                when {
                    isSelectedStartDate -> {
                        state.resetSelection()
                    }
                    isSelectedEndDate -> {
                        val startDate = state.selectedRange.first
                        state.resetSelection()
                        startDate?.let { state.selectDateTime(it) }
                    }
                    else -> {
                        state.selectDateTime(selectedDateTime)
                    }
                }
            }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            color = textColor,
            fontSize = 14.sp,
            lineHeight = 24.sp,
            modifier = Modifier.padding(vertical = 4.dp),
            style = TextStyle(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
    }
}

private fun getMonthLength(year: Int, month: Month): Int {
    return when (month) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> {
            31
        }
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> {
            30
        }
        Month.FEBRUARY -> {
            if (isLeapYear(year)) 29 else 28
        }
        else -> { 30 }
    }
}

private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

private fun getFirstDayOfMonth(year: Int, month: Month): LocalDate {
    return LocalDate(year, month, 1)
}

private fun formatDate(date: LocalDate): String {
    val day = date.dayOfMonth.toString().padStart(2, '0')
    val month = date.monthNumber.toString().padStart(2, '0')
    val year = date.year.toString()
    return "$day.$month.$year"
}

private fun formatTime(dateTime: LocalDateTime): String {
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

private fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}

@Preview
@Composable
fun DateTimeRangeFieldPreview() {
    DateTimeRangeField(
        modifier = Modifier,
        state = rememberDateRangePickerState(),
        onRangeSelected = {}
    )
}