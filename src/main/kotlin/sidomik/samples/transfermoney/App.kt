package sidomik.samples.transfermoney

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import org.slf4j.LoggerFactory
import sidomik.samples.transfermoney.logic.MoneyTransferer
import sidomik.samples.transfermoney.model.Account
import sidomik.samples.transfermoney.model.Transfer
import sidomik.samples.transfermoney.storage.AccountStorage

const val API_ENDPOINT  = "/v1"
const val ACCOUNTS_ENDPOINT = "$API_ENDPOINT/accounts"
const val TRANSFERS_ENDPOINT = "$API_ENDPOINT/transfers"
const val ACCOUNTS_CREATE_ENDPOINT = "$ACCOUNTS_ENDPOINT/create"
const val ACCOUNTS_FIND_ENDPOINT = "$ACCOUNTS_ENDPOINT/:id"

private val logger = LoggerFactory.getLogger("rest-controller")

val restServer: Javalin =
    Javalin.create().apply {
        exception(Exception::class.java) { e, ctx ->
            logger.error("Error while executing request ${ctx.req}", e)
            ctx.status(404)
            ctx.json(e.message ?: "General Error")
        }
    }.routes {
        post(ACCOUNTS_CREATE_ENDPOINT) { ctx ->
            val account = ctx.body<Account>()
            val createdAccount = AccountStorage.create(account)
            ctx.json(createdAccount)
            ctx.status(201)
        }
        get(ACCOUNTS_FIND_ENDPOINT) { ctx ->
            val id = ctx.pathParam("id").toLong()
            ctx.json(AccountStorage.find(id))
            ctx.status(200)
        }
        post(TRANSFERS_ENDPOINT) { ctx ->
            val transfer = ctx.body<Transfer>()
            MoneyTransferer.transferMoney(transfer.from, transfer.to, transfer.amount)
            ctx.status(200)
        }
    }

fun main() {
    restServer.start(7000)
}