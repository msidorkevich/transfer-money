package sidomik.samples.transfermoney.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sidomik.samples.transfermoney.model.Account
import java.lang.IllegalArgumentException

class AccountStorageTest {

    @Test
    fun createAccountFillsId() {
        val account = Account("John Smith", "EUR", 1000.0)

        AccountStorage.create(account)

        assertThat(account.id).isNotEqualTo(-1)
    }

    @Test (expected = IllegalArgumentException::class)
    fun findNonExistingAccount() {
        AccountStorage.find(12345)
    }
}