package sidomik.samples.transfermoney.model

import java.lang.IllegalArgumentException
import java.math.BigDecimal

data class Account(val name: String,
                   var balance: BigDecimal) {
    var id: Long = -1

    fun withDraw(amount: BigDecimal) {
        if (balance < amount) {
            throw IllegalArgumentException("Account $id has balance=$balance less than amount=$amount")

        }
        balance -= amount
    }

    fun deposit(amount: BigDecimal) {
        balance += amount
    }
}