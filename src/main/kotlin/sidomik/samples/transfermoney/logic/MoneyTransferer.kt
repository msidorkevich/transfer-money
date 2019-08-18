package sidomik.samples.transfermoney.logic

import sidomik.samples.transfermoney.exceptions.NonPositiveAmountException
import sidomik.samples.transfermoney.storage.AccountStorage
import java.math.BigDecimal

object MoneyTransferer {

    fun transferMoney(from: Long, to: Long, amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw NonPositiveAmountException(amount)
        }

        if (from < to) {
            transferThreadSafe(from, to, amount)
        } else {
            transferThreadSafe(to, from, -amount)
        }
    }

    private fun transferThreadSafe(from: Long, to: Long, amount: BigDecimal) {
        val fromAccount = AccountStorage.find(from)
        val toAccount = AccountStorage.find(to)

        synchronized(fromAccount) {
            synchronized(toAccount) {
                fromAccount.withDraw(amount)
                toAccount.deposit(amount)
            }
        }
    }
}