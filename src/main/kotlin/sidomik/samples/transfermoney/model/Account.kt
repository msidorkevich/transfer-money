package sidomik.samples.transfermoney.model

import sidomik.samples.transfermoney.exceptions.NotEnoughMoneyException
import java.math.BigDecimal

data class Account(val name: String,
                   var balance: BigDecimal) {
    var id: Long = -1

    fun withDraw(amount: BigDecimal) {
        if (balance < amount) {
            throw NotEnoughMoneyException(id, balance, amount)
        }
        balance -= amount
    }

    fun deposit(amount: BigDecimal) {
        balance += amount
    }
}