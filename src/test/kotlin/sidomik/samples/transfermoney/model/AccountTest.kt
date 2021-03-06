package sidomik.samples.transfermoney.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sidomik.samples.transfermoney.exceptions.NotEnoughMoneyException
import java.math.BigDecimal

class AccountTest {

    private val account = Account("John Smith", BigDecimal("1000.0"))

    @Test (expected = NotEnoughMoneyException::class)
    fun withdrawNotEnoughBalance() {
        account.add(BigDecimal(("-1000.00001")))
    }

    @Test
    fun withdraw() {
        account.add(BigDecimal("-500.0"))

        assertThat(account.balance).isEqualByComparingTo(BigDecimal("500.0"))
    }

    @Test
    fun deposit() {
        account.add(BigDecimal("500.0"))

        assertThat(account.balance).isEqualByComparingTo(BigDecimal("1500.0"))
    }
}