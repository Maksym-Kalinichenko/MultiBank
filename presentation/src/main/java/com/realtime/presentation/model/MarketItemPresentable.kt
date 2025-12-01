package com.realtime.presentation.model

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class MarketItemPresentable(
    val name: String,
    val lastPrice: Double,
    val previousPrice: Double = 0.0
) {
    val changeStatus: Boolean
        get() = (lastPrice >= previousPrice)
    val priceFormat: DecimalFormat =
        DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US)).apply {
            this.maximumFractionDigits = 5
            this.minimumFractionDigits = 5
            this.roundingMode = RoundingMode.DOWN
        }
    val priceFormatted: String = priceFormat.format(lastPrice)
}