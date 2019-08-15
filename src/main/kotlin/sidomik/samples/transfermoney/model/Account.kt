package sidomik.samples.transfermoney.model

import java.lang.IllegalArgumentException

data class Account(val name: String,
                   var balance: Double) {
    var id: Long = -1

    fun withDraw(amount: Double) {
        if (balance < amount) {
            throw IllegalArgumentException("Account $id has balance=$balance less than amount=$amount")

        }
        balance -= amount
    }

    fun deposit(amount: Double) {
        balance += amount
    }
}