package com.cjapps.prop.ui.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.ui.theme.ExtendedTheme
import com.cjapps.prop.ui.theme.ThemeDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentDetailScreen(
    modifier: Modifier = Modifier,
    investmentDetailViewModel: InvestmentDetailViewModel = viewModel(),
    navigateHome: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = ThemeDefaults.pagePadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Ticker Name") },
                    value = "",
                    onValueChange = {},
                    shape = RoundedCornerShape(60.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Current Value ($)") },
                    value = "",
                    onValueChange = {},
                    shape = RoundedCornerShape(60.dp),
                )
            }
            Row(
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PercentageBar(
                        height = 200.dp,
                        width = 60.dp,
                        fillPercentage = 67,
                        backgroundColor = ExtendedTheme.colors.inverseSecondary,
                        fillBrush = Brush.linearGradient(ExtendedTheme.colors.gradientColorList)
                    )
                }
            }
        }
    }
}

@Composable
fun PercentageBar(
    height: Dp,
    width: Dp,
    fillPercentage: Int,
    backgroundColor: Color,
    fillBrush: Brush
) {
    Canvas(
        modifier = Modifier
            .size(width = width, height = height)
    ) {
        val roundedRadius = CornerRadius(width.toPx(), width.toPx())
        val fillHeight = height * (fillPercentage / 100f)

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