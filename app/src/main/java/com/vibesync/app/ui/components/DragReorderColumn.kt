package com.vibesync.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Generic drag-to-reorder list, shared by the playlist detail screen's custom-order view and the
 * Queue screen's "Up Next" list. A plain (non-lazy) [Column], same reasoning as before: item
 * count here is personal-app scale, and a `LazyColumn`'s recycling makes drag-position math
 * meaningfully harder to get right.
 *
 * The dragged row is moved directly via touch (unchanged from the original approach). What's new
 * is a FLIP-style animation ("First, Last, Invert, Play") for every *other* row whose position
 * shifts because of that drag: when [localOrder] mutates, each item that moved is snapped
 * backward by exactly the pixel distance it just jumped (so it visually stays put for one frame)
 * and then animated forward to zero over ~220ms. Without this, a plain reordering list only
 * animates the row you're actually touching -- everything else instantly snaps to its new slot,
 * which is what read as the list "getting stuck on top of another song": the item you dragged
 * past would jump-cut into its new position instead of sliding out of the way.
 */
@Composable
fun <T> DragReorderColumn(
    items: List<T>,
    key: (T) -> Any,
    onOrderChanged: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    rowContent: @Composable (item: T, isDragging: Boolean, dragModifier: Modifier) -> Unit
) {
    var localOrder by remember(items) { mutableStateOf(items) }
    var draggingKey by remember { mutableStateOf<Any?>(null) }
    var draggingIndex by remember { mutableIntStateOf(-1) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var measuredRowHeightPx by remember { mutableFloatStateOf(0f) }

    val itemOffsets = remember { mutableStateMapOf<Any, Animatable<Float, AnimationVector1D>>() }
    var previousIndices by remember { mutableStateOf<Map<Any, Int>>(emptyMap()) }

    androidx.compose.runtime.LaunchedEffect(localOrder) {
        val newIndices = localOrder.mapIndexed { index, item -> key(item) to index }.toMap()
        newIndices.forEach { (itemKey, newIndex) ->
            if (itemKey == draggingKey) return@forEach
            val oldIndex = previousIndices[itemKey]
            if (oldIndex != null && oldIndex != newIndex && measuredRowHeightPx > 0f) {
                val deltaPx = (oldIndex - newIndex) * measuredRowHeightPx
                val anim = itemOffsets.getOrPut(itemKey) { Animatable(0f) }
                launch {
                    anim.snapTo(anim.value + deltaPx)
                    anim.animateTo(0f, animationSpec = tween(220))
                }
            }
        }
        previousIndices = newIndices
    }

    val haptics = LocalHapticFeedback.current

    Column(modifier = modifier) {
        localOrder.forEach { item ->
            val itemKey = key(item)
            val isDragging = itemKey == draggingKey

            val dragModifier = Modifier.pointerInput(itemKey) {
                detectDragGestures(
                    onDragStart = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        draggingKey = itemKey
                        draggingIndex = localOrder.indexOfFirst { key(it) == itemKey }
                        dragOffsetY = 0f
                    },
                    onDragEnd = {
                        draggingKey = null
                        draggingIndex = -1
                        dragOffsetY = 0f
                        onOrderChanged(localOrder)
                    },
                    onDragCancel = {
                        draggingKey = null
                        draggingIndex = -1
                        dragOffsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val current = draggingIndex
                        val rowHeightPx = measuredRowHeightPx
                        if (current < 0 || rowHeightPx <= 0f) return@detectDragGestures
                        dragOffsetY += dragAmount.y
                        val shift = (dragOffsetY / rowHeightPx).roundToInt()
                        val targetIndex = (current + shift).coerceIn(0, localOrder.lastIndex)
                        if (targetIndex != current) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val mutable = localOrder.toMutableList()
                            val moved = mutable.removeAt(current)
                            mutable.add(targetIndex, moved)
                            localOrder = mutable
                            draggingIndex = targetIndex
                            dragOffsetY -= (targetIndex - current) * rowHeightPx
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        if (measuredRowHeightPx <= 0f) measuredRowHeightPx = coordinates.size.height.toFloat()
                    }
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer {
                        translationY = if (isDragging) dragOffsetY else (itemOffsets[itemKey]?.value ?: 0f)
                    }
            ) {
                rowContent(item, isDragging, dragModifier)
            }
        }
    }
}
