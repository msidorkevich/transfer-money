package sidomik.samples.transfermoney.logic

import sidomik.samples.transfermoney.logic.CurrencyConverter.convert
import sidomik.samples.transfermoney.storage.AccountStorage

object MoneyTransferer {

    fun transferMoney(from: Long, to: Long, currency: String, amount: Double) {
        if (from < to) {
            transferThreadSafe(from, to, currency, amount)
        } else {
            transferThreadSafe(to, from, currency, -amount)
        }
    }

    private fun transferThreadSafe(from: Long, to: Long, currency: String, amount: Double) {
        val fromAccount = AccountStorage.find(from)
        val toAccount = AccountStorage.find(to)

        synchronized(fromAccount) {
            synchronized(toAccount) {
                val amountInFromCurrency = convert(currency, fromAccount.currency, amount)
                val amountInToCurrency = convert(currency, toAccount.currency, amount)

                fromAccount.withDraw(amountInFromCurrency)
                toAccount.deposit(amountInToCurrency)
            }
        }
    }
}