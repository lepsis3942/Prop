package com.cjapps.prop.ui.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DraggableNumberSelectionBar(
    height: Dp,
    width: Dp,
    startingNumber: Int,
    maxAllowedNumber: Int,
    backgroundColor: Color,
    fillBrush: Brush,
    numberSelectionUpdated: (Int) -> Unit
) {
    assert(startingNumber <= maxAllowedNumber)
    var runningDragChange by remember { mutableFloatStateOf(0f) }
    // Allow startingNumber to reflect as the updated value on each recomposition in onDragEnd callback
    val updatedStartingNumber by rememberUpdatedState(newValue = startingNumber)

    Box(
        Modifier
            .pointerInput(Unit) {
                detectVerticalDragGestures(onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    val updatedDragValue = runningDragChange + -dragAmount
                    runningDragChange = updatedDragValue
                }, onDragEnd = {
                    numberSelectionUpdated(
                        calculateNumberSelectionFromDragAmount(
                            dragAmount = runningDragChange,
                            composableHeightInPx = height.toPx(),
                            startingNumber = updatedStartingNumber,
                            maxAllowedNumber = maxAllowedNumber
                        )
                    )
                    runningDragChange = 0f
                })
            }
            .padding(horizontal = 40.dp, vertical = 20.dp)) {
        Canvas(
            modifier = Modifier
                .size(width = width, height = height)
        ) {
            val roundedRadius = CornerRadius(width.toPx(), width.toPx())
            val currentNumberSelection = calculateNumberSelectionFromDragAmount(
                dragAmount = runningDragChange,
                composableHeightInPx = height.toPx(),
                startingNumber = updatedStartingNumber,
                maxAllowedNumber = maxAllowedNumber
            )
            val fillHeight = height * (currentNumberSelection / maxAllowedNumber.toFloat())

            val regionPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        Rect(
                            Offset.Zero,
                            size = Size(width = width.toPx(), height = height.toPx())
                        ),
                        topLeft = roundedRadius,
                        topRight = roundedRadius,
                        bottomLeft = roundedRadius,
                        bottomRight = roundedRadius
                    )
                )
            }

            clipPath(
                path = regionPath,
                clipOp = ClipOp.Intersect
            ) {
                drawPath(path = regionPath, color = backgroundColor)
                drawRect(
                    brush = fillBrush,
                    topLeft = Offset(0.dp.toPx(), (height - fillHeight).toPx()),
                    size = Size(width = width.toPx(), height = fillHeight.toPx())
                )
            }
        }
    }
}

private fun calculateNumberSelectionFromDragAmount(
    dragAmount: Float,
    composableHeightInPx: Float,
    startingNumber: Int,
    maxAllowedNumber: Int
): Int {
    val dragChangeResult = ((dragAmount / composableHeightInPx) * maxAllowedNumber)
        .toInt()
    return (startingNumber + dragChangeResult).coerceIn(0, maxAllowedNumber)
}