package sidomik.samples.transfermoney.logic

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sidomik.samples.transfermoney.exceptions.NonExistingAccountException
import sidomik.samples.transfermoney.exceptions.NonPositiveAmountException
import sidomik.samples.transfermoney.exceptions.NotEnoughMoneyException
import sidomik.samples.transfermoney.model.Account
import sidomik.samples.transfermoney.storage.AccountStorage
import java.math.BigDecimal

class MoneyTransfererTest {

    private val account1 = AccountStorage.create(Account("John Smith", BigDecimal("1000.0")))
    private val account2 = AccountStorage.create(Account("Jonny Walker", BigDecimal("1000.0")))

    @Test
    fun fromAccountIdIsSmaller() {
        MoneyTransferer.transferMoney(account1.id, account2.id, BigDecimal("500.0"))

        assertThat(account1.balance).isEqualByComparingTo(BigDecimal("1000.0") - BigDecimal("500.0"))
        assertThat(account2.balance).isEqualByComparingTo(BigDecimal("1000.0") + BigDecimal("500.0"))
    }

    @Test
    fun toAccountIdIsSmaller() {
        MoneyTransferer.transferMoney(account2.id, account1.id, BigDecimal("500.0"))

        assertThat(account1.balance).isEqualByComparingTo(BigDecimal("1000.0") + BigDecimal("500.0"))
        assertThat(account2.balance).isEqualByComparingTo(BigDecimal("1000.0") - BigDecimal("500.0"))
    }

    @Test (expected = NonExistingAccountException::class)
    fun fromAccountDoesntExist() {
        MoneyTransferer.transferMoney(12345, account2.id, BigDecimal("500.0"))
    }

    @Test (expected = NonExistingAccountException::class)
    fun toAccountDoesntExist() {
        MoneyTransferer.transferMoney(account1.id, 12345, BigDecimal("500.0"))
    }

    @Test (expected = NotEnoughMoneyException::class)
    fun fromHasNotEnoughMoney() {
        MoneyTransferer.transferMoney(account1.id, account2.id, BigDecimal("1000.0001"))
    }

    @Test
    fun transferAllTheMoney() {
        MoneyTransferer.transferMoney(account1.id, account2.id, BigDecimal("1000.0"))

        assertThat(account1.balance).isEqualByComparingTo(BigDecimal("1000.0") - BigDecimal("1000.0"))
        assertThat(account2.balance).isEqualByComparingTo(BigDecimal("1000.0") + BigDecimal("1000.0"))
    }

    @Test (expected = NonPositiveAmountException::class)
    fun transferNegativeAmount() {
        MoneyTransferer.transferMoney(account1.id, account2.id, BigDecimal("-0.000001"))
    }

    @Test (expected = NonPositiveAmountException::class)
    fun transferZeroAmount() {
        MoneyTransferer.transferMoney(account1.id, account2.id, BigDecimal.ZERO)
    }
}