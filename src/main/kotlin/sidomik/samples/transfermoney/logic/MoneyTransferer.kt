package sidomik.samples.transfermoney.logic

import sidomik.samples.transfermoney.storage.AccountStorage

object MoneyTransferer {

    fun transferMoney(from: Long, to: Long, amount: Double) {
        if (from < to) {
            transferThreadSafe(from, to, amount)
        } else {
            transferThreadSafe(to, from, -amount)
        }
    }

    private fun transferThreadSafe(from: Long, to: Long, amount: Double) {
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