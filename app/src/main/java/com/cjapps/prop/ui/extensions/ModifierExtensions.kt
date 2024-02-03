package com.cjapps.prop.ui.extensions

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

fun Modifier.fadingEdge(scrollState: ScrollableState) =
    this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            if (scrollState.canScrollBackward) {
                drawRect(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.3f to Color.Red,
                        endY = 25.dp.toPx()
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
            if (scrollState.canScrollForward) {
                drawRect(
                    brush = Brush.verticalGradient(
                        0.7f to Color.Red,
                        1f to Color.Transparent,
                        startY = size.height - 25.dp.toPx(),
                        endY = size.height
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
        }