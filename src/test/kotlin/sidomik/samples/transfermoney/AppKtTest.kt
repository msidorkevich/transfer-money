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
        val response = createAccount(account)
        assertThat(response.code()).isEqualTo(HTTP_CREATED)

        val createdAccount = parseCreateAccountResponse(response)

        assertThat(createdAccount.id).isGreaterThan(0)
        assertThat(createdAccount.name).isEqualTo(account.name)
        assertThat(createdAccount.balance).isEqualTo(account.balance)
    }

    @Test
    fun createAccountWithZeroBalance()  {
        val account = Account("John Smith", BigDecimal.ZERO)
        val response = createAccount(account)
        assertThat(response.code()).isEqualTo(HTTP_CREATED)

        val createdAccount = parseCreateAccountResponse(response)

        assertThat(createdAccount.id).isGreaterThan(0)
        assertThat(createdAccount.name).isEqualTo(account.name)
        assertThat(createdAccount.balance).isEqualTo(account.balance)
    }

    @Test
    fun cantCreateAccountWithNegativeAmount() {
        val account = Account("John Smith", BigDecimal("-0.000001"))
        val response = createAccount(account)

        assertThat(response.code()).isEqualTo(HTTP_BAD_REQUEST)
        assertThat(response.body()?.string()).isEqualTo("Cant create account with negative balance -0.000001")
    }

    @Test
    fun findAccount() {
        val account = Account("Jonny Walker", BigDecimal("123.456"))

        val createAccountResponse = createAccount(account)
        assertThat(createAccountResponse.code()).isEqualTo(HTTP_CREATED)
        val createdAccount = parseCreateAccountResponse(createAccountResponse)

        val findAccountResponse = findAccount(createdAccount.id)
        assertThat(findAccountResponse.code()).isEqualTo(HTTP_OK)
        val foundedAccount = parseFindAccountResponse(findAccountResponse)

        assertThat(foundedAccount).isEqualTo(createdAccount)
    }

    @Test
    fun findNonExistingAccount() {
        val response = findAccount(0)

        assertThat(response.code()).isEqualTo(HTTP_NOT_FOUND)
        assertThat(response.body()?.string()).isEqualTo("Can't find account with id 0")
    }

    @Test
    fun transferMoney() {
        val createAccount1Response = createAccount(Account("John Smith", BigDecimal("1000.0")))
        val createAccount2Response = createAccount(Account("Jonny Walker", BigDecimal("1000.0")))
        assertThat(createAccount1Response.code()).isEqualTo(HTTP_CREATED)
        assertThat(createAccount2Response.code()).isEqualTo(HTTP_CREATED)

        val account1 = parseCreateAccountResponse(createAccount1Response)
        val account2 = parseCreateAccountResponse(createAccount2Response)

        val transferMoneyResponse = transferMoney(account1.id, account2.id, BigDecimal("500.0"))
        assertThat(transferMoneyResponse.code()).isEqualTo(HTTP_OK)

        val findAccount1Response = findAccount(account1.id)
        val findAccount2Response = findAccount(account2.id)
        assertThat(findAccount1Response.code()).isEqualTo(HTTP_OK)
        assertThat(findAccount2Response.code()).isEqualTo(HTTP_OK)

        val account1Balance = parseFindAccountResponse(findAccount1Response).balance
        val account2Balance = parseFindAccountResponse(findAccount2Response).balance

        assertThat(account1Balance).isEqualByComparingTo(BigDecimal("500.0"))
        assertThat(account2Balance).isEqualByComparingTo(BigDecimal("1500.0"))
    }

    @Test
    fun transferMoneyNonExistingAccount() {
        val response = transferMoney(12345, 23456, BigDecimal("500.0"))

        assertThat(response.code()).isEqualTo(HTTP_NOT_FOUND)
        assertThat(response.body()?.string()).isEqualTo("Can't find account with id 12345")
    }

    @Test
    fun transferAccuracy() {
        val account1Response = createAccount(Account("John Smith", BigDecimal("200.123")))
        val account2Response = createAccount(Account("Jonny Walker", BigDecimal("299.877")))
        assertThat(account1Response.code()).isEqualTo(HTTP_CREATED)
        assertThat(account2Response.code()).isEqualTo(HTTP_CREATED)
        val account1 = parseCreateAccountResponse(account1Response)
        val account2 = parseCreateAccountResponse(account2Response)

        val response = transferMoney(account1.id, account2.id, BigDecimal("100.123"))
        assertThat(response.code()).isEqualTo(HTTP_OK)

        val account1Balance = parseFindAccountResponse(findAccount(account1.id)).balance
        val account2Balance = parseFindAccountResponse(findAccount(account2.id)).balance

        assertThat(account1Balance).isEqualByComparingTo(BigDecimal("100.0"))
        assertThat(account2Balance).isEqualByComparingTo(BigDecimal("400.0"))
    }

    @Test
    fun transferAmountCantBeNegative() {
        val createAccount1Response = createAccount(Account("John Smith", BigDecimal("1000.0")))
        val createAccount2Response = createAccount(Account("Jonny Walker", BigDecimal("1000.0")))
        assertThat(createAccount1Response.code()).isEqualTo(HTTP_CREATED)
        assertThat(createAccount2Response.code()).isEqualTo(HTTP_CREATED)
        val account1 = parseCreateAccountResponse(createAccount1Response)
        val account2 = parseCreateAccountResponse(createAccount2Response)

        val response = transferMoney(account1.id, account2.id, BigDecimal("-0.00000001"))
        assertThat(response.code()).isEqualTo(HTTP_BAD_REQUEST)
        assertThat(response.body()?.string()).isEqualTo("Amount should be positive, but is -0.00000001")
    }

    @Test
    fun transferAmountCantBeZero() {
        val createAccount1Response = createAccount(Account("John Smith", BigDecimal("1000.0")))
        val createAccount2Response = createAccount(Account("Jonny Walker", BigDecimal("1000.0")))
        assertThat(createAccount1Response.code()).isEqualTo(HTTP_CREATED)
        assertThat(createAccount2Response.code()).isEqualTo(HTTP_CREATED)
        val account1 = parseCreateAccountResponse(createAccount1Response)
        val account2 = parseCreateAccountResponse(createAccount2Response)

        val response = transferMoney(account1.id, account2.id, BigDecimal.ZERO)
        assertThat(response.code()).isEqualTo(HTTP_BAD_REQUEST)
        assertThat(response.body()?.string()).isEqualTo("Amount should be positive, but is 0")
    }

    private fun createAccount(account: Account): Response {
        return httpPost {
            host = "localhost"
            port = 8000
            path = ACCOUNTS_CREATE_ENDPOINT

            body("application/json") {
                json(jsonMapper.writeValueAsString(account))
            }
        }
    }

    private fun parseCreateAccountResponse(response: Response): Account {
        return jsonMapper.readValue(response.body()?.string(), Account::class.java)
    }

    private fun findAccount(id: Long): Response {
        return httpGet {
            host = "localhost"
            port = 8000
            path = "$ACCOUNTS_ENDPOINT/$id"
        }
    }

    private fun parseFindAccountResponse(response: Response): Account {
        return jsonMapper.readValue(response.body()?.string(), Account::class.java)
    }

    private fun transferMoney(from: Long, to: Long, amount: BigDecimal): Response {
        return httpPost {
            host = "localhost"
            port = 8000
            path = TRANSFERS_ENDPOINT

            body("application/json") {
                json(jsonMapper.writeValueAsString(Transfer(from, to, amount)))
            }
        }
    }
}