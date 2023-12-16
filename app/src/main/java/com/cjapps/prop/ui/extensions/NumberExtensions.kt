package com.cjapps.prop.ui.extensions

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