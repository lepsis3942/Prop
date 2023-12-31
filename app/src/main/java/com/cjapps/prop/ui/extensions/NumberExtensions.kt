package com.cjapps.prop.ui.extensions

import android.icu.text.NumberFormat
import java.math.BigDecimal
import java.text.DecimalFormat

fun BigDecimal.asDisplayCurrency(): String {
    val formatter = DecimalFormat.getCurrencyInstance()
    return formatter.format(this)
}

fun BigDecimal.asDisplayPercentage(): String {
    val formatter = DecimalFormat.getPercentInstance()
    return formatter.format(this)
}

fun BigDecimal.isNumericalValueEqualTo(otherValue: BigDecimal): Boolean {
    return this.compareTo(otherValue) == 0
}

/**
 * Convert raw input to a BigDecimal. Input will format currency as a series of numbers:
 * EX: 78023.91 is represented as 7802391
 * EX: 101.00 is represented as 10100
 * EX: 0.01 is represented as 1
 */
fun String.rawCurrencyInputToBigDecimal(): BigDecimal {
    var formattedInput = this.filter { it.isDigit() }
    if (formattedInput.length < 2) {
        formattedInput = formattedInput.padStart(2, '0')
    }
    if (formattedInput.length == 2) {
        formattedInput = "0$formattedInput"
    }

    formattedInput =
        "${formattedInput.substring(0..formattedInput.length - 3)}.${formattedInput.takeLast(2)}"

    return BigDecimal(formattedInput)
}

/**
 * Convert BigDecimal to raw currency string. Input will format currency as a series of numbers:
 * EX: 78023.91 is represented as 7802391
 * EX: 101.00 is represented as 10100
 * EX: 0.01 is represented as 1
 */
fun BigDecimal.bigDecimalToRawCurrency(): String {
    val splitDecimal = this.divideAndRemainder(BigDecimal.ONE)
    val intPart = splitDecimal[0].toInt().toString()
    val fractionalPart = splitDecimal[1].toString()
        .drop(2)
        .take(2)
        .padEnd(2, '0')
    return intPart + fractionalPart
}

fun BigDecimal.bigDecimalToUiFormattedCurrency(): String {
    val formatter = NumberFormat.getCurrencyInstance()
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    formatter.minimumIntegerDigits = 1
    return formatter.format(this)
}