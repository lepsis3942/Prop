package com.cjapps.prop.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun animatingButtonColors(
    buttonColors: ButtonColors,
    isButtonEnabled: Boolean
): ButtonColors {
    val isEnabled by rememberUpdatedState(newValue = isButtonEnabled)
    val buttonContainerColor = buttonColors.containerColor
    val buttonContentColor = buttonColors.contentColor
    val buttonDisabledContainerColor = buttonColors.disabledContainerColor
    val buttonDisabledContentColor = buttonColors.disabledContentColor
    val animateButtonColorState =
        animateColorAsState(
            targetValue = if (isEnabled) buttonContainerColor else buttonDisabledContainerColor,
            animationSpec = tween(250, 0, FastOutLinearInEasing),
            label = "Button Color Animation"
        )
    val animateButtonContentColorState = animateColorAsState(
        targetValue = if (isEnabled) buttonContentColor else buttonDisabledContentColor,
        animationSpec = tween(150, 0, FastOutLinearInEasing),
        label = "Button Content Color Animation"
    )
    return ButtonColors(
        containerColor = animateButtonColorState.value,
        disabledContainerColor = animateButtonColorState.value,
        contentColor = animateButtonContentColorState.value,
        disabledContentColor = animateButtonContentColorState.value,
    )
}