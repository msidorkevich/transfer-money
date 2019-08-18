package sidomik.samples.transfermoney.model

import sidomik.samples.transfermoney.exceptions.NotEnoughMoneyException
import java.math.BigDecimal

data class Account(val name: String,
                   @Volatile var balance: BigDecimal) {
    var accountId: AccountId = AccountId(-1)

    fun add(amount: BigDecimal) {
        val newBalance = balance + amount
        if (newBalance < BigDecimal.ZERO) {
            throw NotEnoughMoneyException(accountId, balance, amount)
        }
        balance = newBalance
    }
}

data class AccountId(val id: Long)