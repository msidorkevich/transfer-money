package sidomik.samples.transfermoney.logic

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.Test
import sidomik.samples.transfermoney.logic.CurrencyConverter.convert
import sidomik.samples.transfermoney.logic.CurrencyConverter.eurRubRate
import sidomik.samples.transfermoney.logic.CurrencyConverter.eurUsdRate
import sidomik.samples.transfermoney.logic.CurrencyConverter.usdRubRate
import java.lang.IllegalArgumentException

class CurrencyConverterTest {

    @Test
    fun fromEqualTo() {
        assertThat(convert("RUB", "RUB", 100.0)).isCloseTo(100.0, withPercentage(0.001))
    }

    @Test
    fun eurUsd() {
        assertThat(convert("EUR", "USD", 1.0)).isCloseTo(eurUsdRate, withPercentage(0.001))
    }

    @Test
    fun usdEur() {
        assertThat(convert("USD", "EUR", eurUsdRate)).isCloseTo(1.0, withPercentage(0.001))
    }

    @Test
    fun eurRub() {
        assertThat(convert("EUR", "RUB", 1.0)).isCloseTo(eurRubRate, withPercentage(0.001))
    }

    @Test
    fun rubEur() {
        assertThat(convert("RUB", "EUR", eurRubRate)).isCloseTo(1.0, withPercentage(0.001))
    }

    @Test
    fun usdRub() {
        assertThat(convert("USD", "RUB", 1.0)).isCloseTo(usdRubRate, withPercentage(0.001))
    }

    @Test
    fun rubUsd() {
        assertThat(convert("RUB", "USD", usdRubRate)).isCloseTo(1.0, withPercentage(0.001))
    }

    @Test (expected = IllegalArgumentException::class)
    fun unsupportedCurrency() {
        convert("EUR", "SEK", 1000.0)
    }
}