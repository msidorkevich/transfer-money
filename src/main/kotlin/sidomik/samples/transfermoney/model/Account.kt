package sidomik.samples.transfermoney.model

import sidomik.samples.transfermoney.exceptions.NotEnoughMoneyException
import java.math.BigDecimal

data class Account(val name: String,
                   @Volatile var balance: BigDecimal) {
    var accountId: AccountId = AccountId(-1)

    fun withdraw(amount: BigDecimal) {
        if (balance < amount) {
            throw NotEnoughMoneyException(accountId, balance, amount)
        }
        balance -= amount
    }

    fun deposit(amount: BigDecimal) {
        balance += amount
    }
}

data class AccountId(val id: Long)