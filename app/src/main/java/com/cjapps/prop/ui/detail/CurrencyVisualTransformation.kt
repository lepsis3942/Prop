package com.cjapps.prop.ui.detail

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import kotlin.math.abs

class CurrencyVisualTransformation : VisualTransformation {
    private val decimalFormat = DecimalFormat()
    private val groupingSeparator by lazy {
        decimalFormat.decimalFormatSymbols.groupingSeparator
    }
    private val decimalSeparator by lazy {
        decimalFormat.decimalFormatSymbols.decimalSeparator
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val inputText = text.text.filter { it.isDigit() }

        val intPortion = if (inputText.length > 2) {
            inputText.subSequence(0, inputText.length - 2)
        } else {
            "0"
        }

        val fractionPortion = if (inputText.length < 2) {
            inputText.padStart(2, '0')
        } else {
            inputText.subSequence(inputText.length - 2, inputText.length)
        }

        val intPortionWithSeparators =
            intPortion.foldRightIndexed("") { index: Int, c: Char, stringSoFar: String ->
                // Gets us how far along we are from the back of the string
                val progressInt = abs(index - (intPortion.length))
                if (progressInt != 0 && progressInt != intPortion.length && progressInt % 3 == 0)
                    "$groupingSeparator$c$stringSoFar" else "$c$stringSoFar"
            }

        val transformedText = "$intPortionWithSeparators$decimalSeparator$fractionPortion"
        val offsetMapping =
            ThousandSeparatorOffsetMapping(
                inputText,
                transformedText
            )
        return TransformedText(
            AnnotatedString(
                transformedText,
                text.spanStyles,
                text.paragraphStyles,
            ),
            offsetMapping
        )
    }

    private class ThousandSeparatorOffsetMapping(
        private val unformattedText: String,
        private val formattedText: String
    ) :
        OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return formattedText.length
        }

        override fun transformedToOriginal(offset: Int): Int {
            return unformattedText.length
        }
    }
}