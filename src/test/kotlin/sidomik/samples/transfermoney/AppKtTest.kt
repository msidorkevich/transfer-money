package sidomik.samples.transfermoney

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.dsl.httpPost
import okhttp3.Response
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import sidomik.samples.transfermoney.model.Account
import sidomik.samples.transfermoney.model.Transfer


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
        val account = Account("John Smith", "EUR", 123.456)
        val actualAccount = createAccount(account)

        assertThat(actualAccount.id).isGreaterThan(0)
        assertThat(actualAccount.name).isEqualTo(account.name)
        assertThat(actualAccount.currency).isEqualTo(account.currency)
        assertThat(actualAccount.balance).isEqualTo(account.balance)
    }

    @Test
    fun findExistingAccount() {
        val account = Account("Jonny Walker", "USD", 123.456)
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
        val account1 = createAccount(Account("John Smith", "EUR", 1000.0))
        val account2 = createAccount(Account("Jonny Walker", "EUR", 1000.0))

        transferMoney(account1.id, account2.id, "EUR", 500.0)

        val account1Balance = findExistingAccount(account1.id).balance
        val account2Balance = findExistingAccount(account2.id).balance

        assertThat(account1Balance).isCloseTo(500.0, withPercentage(0.001))
        assertThat(account2Balance).isCloseTo(1500.0, withPercentage(0.001))
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

    private fun transferMoney(from: Long, to: Long, currency: String, amount: Double) {
        val response: Response = httpPost {
            host = "localhost"
            port = 8000
            path = TRANSFERS_ENDPOINT

            body("application/json") {
                json(jsonMapper.writeValueAsString(Transfer(from, to, currency, amount)))
            }
        }

        response.use {
            assertThat(it.code()).isEqualTo(200)
        }
    }
}