package sidomik.samples.transfermoney.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sidomik.samples.transfermoney.exceptions.NotEnoughMoneyException
import java.math.BigDecimal

class AccountTest {

    private val account = Account("John Smith", BigDecimal("1000.0"))

    @Test (expected = NotEnoughMoneyException::class)
    fun notEnoughBalance() {
        account.withdraw(BigDecimal(("1000.00001")))
    }

    @Test
    fun withDraw() {
        account.withdraw(BigDecimal("500.0"))

        assertThat(account.balance).isEqualByComparingTo(BigDecimal("500.0"))
    }

    @Test
    fun deposit() {
        account.deposit(BigDecimal("500.0"))

        assertThat(account.balance).isEqualByComparingTo(BigDecimal("1500.0"))
    }
}