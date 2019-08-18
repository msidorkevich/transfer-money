package sidomik.samples.transfermoney.logic

import com.google.common.collect.Interners
import sidomik.samples.transfermoney.exceptions.NonPositiveAmountException
import sidomik.samples.transfermoney.logic.LockManager.sync
import sidomik.samples.transfermoney.model.AccountId
import sidomik.samples.transfermoney.storage.AccountStorage
import java.math.BigDecimal

object MoneyTransferer {
    fun transferMoney(from: AccountId, to: AccountId, amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw NonPositiveAmountException(amount)
        }

        if (from.id < to.id) {
            transferThreadSafe(from, to, amount)
        } else {
            transferThreadSafe(to, from, -amount)
        }
    }

    private fun transferThreadSafe(from: AccountId, to: AccountId, amount: BigDecimal) {
        val fromAccount = AccountStorage.find(from)
        val toAccount = AccountStorage.find(to)

        sync(fromAccount.accountId) {
            sync(toAccount.accountId) {
                fromAccount.withdraw(amount)
                toAccount.deposit(amount)
            }
        }
    }
}

object LockManager {
    private val interner = Interners.newWeakInterner<AccountId>()

    fun sync(accountId: AccountId, block: () -> Unit) {
        val lock = interner.intern(accountId)
        synchronized(lock, block)
    }
}