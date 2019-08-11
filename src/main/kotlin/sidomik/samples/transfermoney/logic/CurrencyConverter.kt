package sidomik.samples.transfermoney.logic

import java.lang.IllegalArgumentException

object CurrencyConverter {

    const val eurUsdRate = 1.13
    const val eurRubRate = 73.5
    const val usdRubRate = 65.4

    fun convert(from: String, to: String, amount: Double): Double {
        if (from == to) return amount
        if (from == "EUR" && to == "USD") return amount * eurUsdRate
        if (from == "USD" && to == "EUR") return amount / eurUsdRate
        if (from == "EUR" && to == "RUB") return amount * eurRubRate
        if (from == "RUB" && to == "EUR") return amount / eurRubRate
        if (from == "USD" && to == "RUB") return amount * usdRubRate
        if (from == "RUB" && to == "USD") return amount / usdRubRate

        throw IllegalArgumentException("Our bank support EUR, USD, RUB currencies only")
    }
}