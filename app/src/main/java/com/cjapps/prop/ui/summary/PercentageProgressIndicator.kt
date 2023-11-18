package com.cjapps.prop.ui.summary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp

@Composable
fun PercentageProgressIndicator(
    modifier: Modifier = Modifier,
    currentProgress: Int,
    backgroundColor: Color,
    barHeight: Dp,
    fillBrush: Brush,
) {
    val localDensity = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val percentageTextStyle =
        MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.onSurface)
    val measuredProgressText = textMeasurer.measure(
        "$currentProgress%", style = percentageTextStyle,
    )
    val progressTextSizeHeightDp = with(localDensity) { measuredProgressText.size.height.toDp() }
    val progressTextSpacingDp = with(localDensity) { 5.toDp() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight + progressTextSizeHeightDp + progressTextSpacingDp)
    ) {
        val roundedRadius = CornerRadius(600F, 600F)
        val progressXOffset = size.width * (currentProgress.coerceIn(0, 100) / 100F)
        val maxTextXOffset = size.width - measuredProgressText.size.width
        val textXOffset =
            (progressXOffset - (measuredProgressText.size.width / 2)).coerceIn(0F, maxTextXOffset)

        val regionPath = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(
                        Offset.Zero,
                        size = Size(width = size.width, height = barHeight.toPx())
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
                size = Size(width = progressXOffset, height = barHeight.toPx())
            )
        }

        drawText(
            textLayoutResult = measuredProgressText,
            topLeft = Offset(textXOffset, barHeight.toPx() + progressTextSpacingDp.toPx())
        )
    }
}