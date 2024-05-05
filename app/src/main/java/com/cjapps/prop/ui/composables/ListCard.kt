package com.cjapps.prop.ui.composables

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun ListCard(
    modifier: Modifier,
    onTap: (() -> Unit)? = null,
    content: @Composable () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(
        alpha = 0.4f
    )
) {
    Card(
        modifier = modifier.pointerInput(Unit) {
            if (onTap != null) {
                detectTapGestures {
                    onTap()
                }
            }
        }, colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        content()
    }
}