package sidomik.samples.transfermoney

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import org.slf4j.LoggerFactory
import sidomik.samples.transfermoney.exceptions.NegativeBalanceException
import sidomik.samples.transfermoney.exceptions.NonExistingAccountException
import sidomik.samples.transfermoney.exceptions.NonPositiveAmountException
import sidomik.samples.transfermoney.exceptions.NotEnoughMoneyException
import sidomik.samples.transfermoney.logic.MoneyTransferer
import sidomik.samples.transfermoney.model.Account
import sidomik.samples.transfermoney.model.Transfer
import sidomik.samples.transfermoney.storage.AccountStorage

const val API_ENDPOINT  = "/v1"
const val ACCOUNTS_ENDPOINT = "$API_ENDPOINT/accounts"
const val TRANSFERS_ENDPOINT = "$API_ENDPOINT/transfers"
const val ACCOUNTS_CREATE_ENDPOINT = "$ACCOUNTS_ENDPOINT/create"
const val ACCOUNTS_FIND_ENDPOINT = "$ACCOUNTS_ENDPOINT/:id"

const val HTTP_OK = 200
const val HTTP_CREATED = 201
const val HTTP_BAD_REQUEST = 400
const val HTTP_NOT_FOUND = 404

private val logger = LoggerFactory.getLogger("rest-controller")

val restServer: Javalin =
    Javalin.create().apply {
        exception(NegativeBalanceException::class.java) { e, ctx ->
            val errorMessage = "Cant create account with negative balance ${e.balance}"
            logger.error(errorMessage, e)
            ctx.status(HTTP_BAD_REQUEST)
            ctx.result(errorMessage)
        }
        exception(NonExistingAccountException::class.java) { e, ctx ->
            val errorMessage = "Can't find account with id ${e.id}"
            logger.error(errorMessage, e)
            ctx.status(HTTP_NOT_FOUND)
            ctx.result(errorMessage)
        }
        exception(NonPositiveAmountException::class.java) { e, ctx ->
            val errorMessage = "Amount should be positive, but is ${e.amount.toPlainString()}"
            logger.error(errorMessage, e)
            ctx.status(HTTP_BAD_REQUEST)
            ctx.result(errorMessage)
        }
        exception(NotEnoughMoneyException::class.java) { e, ctx ->
            val errorMessage = "Not enough money on account ${e.id} to withdraw ${e.amount}, current balance is ${e.balance}"
            logger.error(errorMessage, e)
            ctx.status(HTTP_BAD_REQUEST)
            ctx.result(errorMessage)
        }
        exception(Exception::class.java) { e, ctx ->
            logger.error("Error while executing request ${ctx.req}", e)
            ctx.status(HTTP_NOT_FOUND)
            ctx.result(e.message ?: "General Error")
        }
    }.routes {
        post(ACCOUNTS_CREATE_ENDPOINT) { ctx ->
            val account = ctx.body<Account>()
            val createdAccount = AccountStorage.create(account)
            ctx.json(createdAccount)
            ctx.status(HTTP_CREATED)
        }
        get(ACCOUNTS_FIND_ENDPOINT) { ctx ->
            val id = ctx.pathParam("id").toLong()
            ctx.json(AccountStorage.find(id))
            ctx.status(HTTP_OK)
        }
        post(TRANSFERS_ENDPOINT) { ctx ->
            val transfer = ctx.body<Transfer>()
            MoneyTransferer.transferMoney(transfer.from, transfer.to, transfer.amount)
            ctx.status(HTTP_OK)
        }
    }

fun main() {
    restServer.start(7000)
}