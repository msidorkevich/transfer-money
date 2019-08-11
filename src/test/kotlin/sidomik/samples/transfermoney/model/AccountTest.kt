package sidomik.samples.transfermoney.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.Test

class AccountTest {

    val account = Account("John Smith", "EUR", 1000.0)

    @Test (expected = IllegalArgumentException::class)
    fun notEnoughBalance() {
        account.withDraw(1000.00001)
    }

    @Test
    fun withDraw() {
        account.withDraw(500.0)

        assertThat(account.balance).isCloseTo(500.0, withPercentage(0.001))
    }

    @Test
    fun deposit() {
        account.deposit(500.0)

        assertThat(account.balance).isCloseTo(1500.0, withPercentage(0.001))
    }
}