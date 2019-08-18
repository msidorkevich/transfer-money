package sidomik.samples.transfermoney.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sidomik.samples.transfermoney.exceptions.NegativeBalanceException
import sidomik.samples.transfermoney.exceptions.NonExistingAccountException
import sidomik.samples.transfermoney.model.Account
import sidomik.samples.transfermoney.model.AccountId
import java.math.BigDecimal

class AccountStorageTest {

    @Test
    fun createAccountCheckAllFields() {
        val account = Account("John Smith", BigDecimal("1000.0"))

        AccountStorage.create(account)

        assertThat(account.accountId).isNotEqualTo(-1)
        assertThat(account.name).isEqualTo("John Smith")
        assertThat(account.balance).isEqualTo(BigDecimal("1000.0"))
    }

    @Test
    fun createAccountWithZeroBalance() {
        val account = Account("John Smith", BigDecimal.ZERO)

        AccountStorage.create(account)

        assertThat(account.accountId).isNotEqualTo(-1)
        assertThat(account.name).isEqualTo("John Smith")
        assertThat(account.balance).isEqualTo(BigDecimal.ZERO)
    }

    @Test (expected = NegativeBalanceException::class)
    fun cantCreateAccountWithNegativeBalance() {
        val account = Account("John Smith", BigDecimal("-0.000001"))

        AccountStorage.create(account)
    }

    @Test (expected = NonExistingAccountException::class)
    fun findNonExistingAccount() {
        AccountStorage.find(AccountId(12345))
    }
}