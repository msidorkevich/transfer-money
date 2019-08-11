package sidomik.samples.transfermoney.logic

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.Test
import sidomik.samples.transfermoney.logic.CurrencyConverter.eurRubRate
import sidomik.samples.transfermoney.logic.CurrencyConverter.eurUsdRate
import sidomik.samples.transfermoney.model.Account
import sidomik.samples.transfermoney.storage.AccountStorage

class MoneyTransfererTest {

    private val account1 = AccountStorage.create(Account("John Smith", "EUR", 1000.0))
    private val account2 = AccountStorage.create(Account("Jonny Walker", "USD", 1000.0))
    private val account3 = AccountStorage.create(Account("Ivan Ivanov", "RUB", 1000.0))

    @Test
    fun fromAccountIdIsSmaller() {
        MoneyTransferer.transferMoney(account1.id, account2.id, "EUR", 500.0)

        assertThat(account1.balance).isCloseTo(1000.0 - 500.0, withPercentage(0.001))
        assertThat(account2.balance).isCloseTo(1000.0 + 500.0 * eurUsdRate, withPercentage(0.001))
    }

    @Test
    fun toAccountIdIsSmaller() {
        MoneyTransferer.transferMoney(account2.id, account1.id, "EUR", 500.0)

        assertThat(account1.balance).isCloseTo(1000.0 + 500.0, withPercentage(0.001))
        assertThat(account2.balance).isCloseTo(1000.0 - 500.0 * eurUsdRate, withPercentage(0.001))
    }

    @Test (expected = IllegalArgumentException::class)
    fun fromAccountDoesntExist() {
        MoneyTransferer.transferMoney(12345, account2.id, "EUR", 500.0)
    }

    @Test (expected = IllegalArgumentException::class)
    fun toAccountDoesntExist() {
        MoneyTransferer.transferMoney(account1.id, 12345, "EUR", 500.0)
    }

    @Test (expected = IllegalArgumentException::class)
    fun fromHasNotEnoughMoney() {
        MoneyTransferer.transferMoney(account1.id, account2.id, "EUR", 1000.0001)
    }

    @Test
    fun threeCurrencies() {
        MoneyTransferer.transferMoney(account2.id, account3.id, "EUR", 500.0)

        assertThat(account2.balance).isCloseTo(1000.0 - 500.0 * eurUsdRate, withPercentage(0.001))
        assertThat(account3.balance).isCloseTo(1000.0 + 500.0 * eurRubRate, withPercentage(0.001))
    }
}