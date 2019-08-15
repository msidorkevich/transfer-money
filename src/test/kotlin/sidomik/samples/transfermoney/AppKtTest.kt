package sidomik.samples.transfermoney

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.dsl.httpPost
import okhttp3.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import sidomik.samples.transfermoney.model.Account
import sidomik.samples.transfermoney.model.Transfer
import java.math.BigDecimal


class AppKtTest {

    private val jsonMapper = jacksonObjectMapper()

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            restServer.start(8000)
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            restServer.stop()
        }
    }

    @Test
    fun createAccount()  {
        val account = Account("John Smith", BigDecimal("123.456"))
        val actualAccount = createAccount(account)

        assertThat(actualAccount.id).isGreaterThan(0)
        assertThat(actualAccount.name).isEqualTo(account.name)
        assertThat(actualAccount.balance).isEqualTo(account.balance)
    }

    @Test
    fun findExistingAccount() {
        val account = Account("Jonny Walker", BigDecimal("123.456"))
        val createdAccount = createAccount(account)
        val foundedAccount = findExistingAccount(createdAccount.id)

        assertThat(foundedAccount).isEqualTo(createdAccount)
    }

    @Test
    fun findNonExistingAccount() {
        findNonExistingAccount(0)
    }

    @Test
    fun transferMoney() {
        val account1 = createAccount(Account("John Smith", BigDecimal("1000.0")))
        val account2 = createAccount(Account("Jonny Walker", BigDecimal("1000.0")))

        transferMoneySuccess(account1.id, account2.id, BigDecimal("500.0"))

        val account1Balance = findExistingAccount(account1.id).balance
        val account2Balance = findExistingAccount(account2.id).balance

        assertThat(account1Balance).isEqualByComparingTo(BigDecimal("500.0"))
        assertThat(account2Balance).isEqualByComparingTo(BigDecimal("1500.0"))
    }

    @Test
    fun transferMoneyNonExistingAccount() {
        transferMoneyNotSuccess(12345, 23456, BigDecimal("500.0"))
    }

    @Test
    fun transferAccuracy() {
        val account1 = createAccount(Account("John Smith", BigDecimal("200.123")))
        val account2 = createAccount(Account("Jonny Walker", BigDecimal("299.877")))

        transferMoneySuccess(account1.id, account2.id, BigDecimal("100.123"))

        val account1Balance = findExistingAccount(account1.id).balance
        val account2Balance = findExistingAccount(account2.id).balance

        assertThat(account1Balance).isEqualByComparingTo(BigDecimal("100.0"))
        assertThat(account2Balance).isEqualByComparingTo(BigDecimal("400.0"))
    }

    private fun createAccount(account: Account): Account {
        val response: Response = httpPost {
            host = "localhost"
            port = 8000
            path = ACCOUNTS_CREATE_ENDPOINT

            body("application/json") {
                json(jsonMapper.writeValueAsString(account))
            }
        }

        response.use {
            assertThat(it.code()).isEqualTo(201)
            return jsonMapper.readValue(it.body()?.string(), Account::class.java)
        }
    }

    private fun findExistingAccount(id: Long): Account {
        val response: Response = httpGet {
            host = "localhost"
            port = 8000
            path = "$ACCOUNTS_ENDPOINT/$id"
        }

        response.use {
            assertThat(it.code()).isEqualTo(200)
            return jsonMapper.readValue(it.body()?.string(), Account::class.java)
        }
    }

    private fun findNonExistingAccount(id: Long) {
        val response: Response = httpGet {
            host = "localhost"
            port = 8000
            path = "$ACCOUNTS_ENDPOINT/$id"
        }

        response.use {
            assertThat(it.code()).isEqualTo(404)
        }
    }

    private fun transferMoneySuccess(from: Long, to: Long, amount: BigDecimal) {
        val response: Response = httpPost {
            host = "localhost"
            port = 8000
            path = TRANSFERS_ENDPOINT

            body("application/json") {
                json(jsonMapper.writeValueAsString(Transfer(from, to, amount)))
            }
        }

        response.use {
            assertThat(it.code()).isEqualTo(200)
        }
    }

    private fun transferMoneyNotSuccess(from: Long, to: Long, amount: BigDecimal) {
        val response: Response = httpPost {
            host = "localhost"
            port = 8000
            path = TRANSFERS_ENDPOINT

            body("application/json") {
                json(jsonMapper.writeValueAsString(Transfer(from, to, amount)))
            }
        }

        response.use {
            assertThat(it.code()).isEqualTo(404)
        }
    }
}