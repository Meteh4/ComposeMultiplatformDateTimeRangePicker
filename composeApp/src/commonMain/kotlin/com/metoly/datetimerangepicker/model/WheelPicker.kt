package com.metoly.datetimerangepicker.model

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.abs

/**
 * State class for the [VerticalWheelPicker].
 *
 * @param initialIndex The initial index of the selected item.
 */
class WheelPickerState(
    initialIndex: Int = 0,
) {
    private val _currentIndex = mutableIntStateOf(initialIndex.coerceAtLeast(0))
    private val _currentIndexSnapshot = mutableIntStateOf(initialIndex.coerceAtLeast(0))

    val currentIndex: Int get() = _currentIndex.intValue
    val currentIndexSnapshot: Int get() = _currentIndexSnapshot.intValue

    internal val lazyListState = LazyListState(
        firstVisibleItemIndex = (initialIndex).coerceAtLeast(0)
    )

    internal fun updateCurrentIndex(index: Int) {
        _currentIndex.intValue = index
    }

    internal fun updateCurrentIndexSnapshot(index: Int) {
        _currentIndexSnapshot.intValue = index
    }

    suspend fun scrollToIndex(index: Int, animate: Boolean = true) {
        val safeIndex = index.coerceAtLeast(0)
        if (animate) {
            lazyListState.animateScrollToItem(safeIndex)
        } else {
            lazyListState.scrollToItem(safeIndex)
        }
    }
}

@Composable
fun rememberWheelPickerState(initialIndex: Int = 0): WheelPickerState {
    return remember { WheelPickerState(initialIndex) }
}

/**
 * A vertical wheel picker composable that allows the user to select a value from a list by scrolling.
 *
 * @param modifier Modifier for styling and layout adjustments.
 * @param count The total number of items in the picker. Must be greater than 0.
 * @param state The state of the wheel picker, used to control and observe the selected item.
 * Defaults to a remembered [WheelPickerState].
 * @param itemHeight The height of each item in the picker. Defaults to 48.dp.
 * @param unfocusedCount The number of items visible above and below the selected item.
 * Determines the overall height of the picker and the amount of context visible to the user.
 * Must be greater than 0.  Defaults to 1, resulting in 3 visible items.
 * @param content A composable function that defines the content to display for each item in the picker,
 * based on its index.  The index of the item is passed as a parameter.
 *
 * @throws IllegalArgumentException if `count` or `unfocusedCount` are not positive.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalWheelPicker(
    modifier: Modifier = Modifier,
    count: Int,
    state: WheelPickerState = rememberWheelPickerState(),
    itemHeight: Dp = 48.dp,
    unfocusedCount: Int = 1,
    content: @Composable (index: Int) -> Unit
) {
    check(count > 0) { "The count must be positive" }
    check(unfocusedCount > 0) { "The unfocusedCount must be positive" }

    val visibleItemCount = 2 * unfocusedCount + 1
    val listState = state.lazyListState
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val currentItem by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) return@derivedStateOf 0

            val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
            val viewportCenter = layoutInfo.viewportStartOffset + viewportHeight / 2

            val closest = visibleItemsInfo.minByOrNull {
                abs((it.offset + it.size / 2) - viewportCenter)
            }

            closest?.index ?: 0
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { currentItem }
            .distinctUntilChanged()
            .collect { index ->
                state.updateCurrentIndexSnapshot(index)
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .map { it }
            .collect { isScrolling ->
                if (!isScrolling) {
                    state.updateCurrentIndex(currentItem)
                }
            }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemCount)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(
                top = itemHeight * unfocusedCount,
                bottom = itemHeight * unfocusedCount
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            userScrollEnabled = true
        ) {
            items(count) { index ->
                val isSelected = index == state.currentIndexSnapshot

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val layoutInfo = listState.layoutInfo
                    val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
                    val viewportCenter = layoutInfo.viewportStartOffset + viewportHeight / 2
                    val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
                    val normalizedDistance = if (itemInfo != null) {
                        val itemCenter = itemInfo.offset + (itemInfo.size / 2)
                        val pixelDistanceFromCenter = abs(itemCenter - viewportCenter)

                        with(LocalDensity.current) {
                            (pixelDistanceFromCenter / (itemHeight.toPx() * 1.5f)).coerceIn(0f, 1f)
                        }
                    } else {
                        1f
                    }

                    WheelPickerItem(
                        isSelected = isSelected,
                        distanceFromCenter = normalizedDistance,
                        content = { content(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WheelPickerItem(
    isSelected: Boolean,
    distanceFromCenter: Float,
    content: @Composable () -> Unit
) {
    val scale = remember(distanceFromCenter) {
        val maxScale = 1.3f
        val minScale = 0.8f
        val scaleRange = maxScale - minScale

        val clampedDistance = distanceFromCenter.coerceAtMost(1f)
        maxScale - (clampedDistance * scaleRange)
    }

    Box(
        modifier = Modifier
            .alpha(if (isSelected) 1f else 0.5f)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun NumberPickerItem(
    number: Int,
    isSelected: Boolean = false
) {
    Text(
        text = number.toString().padStart(2, '0'),
        fontSize = 36.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )
}