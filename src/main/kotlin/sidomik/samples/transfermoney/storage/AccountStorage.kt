package sidomik.samples.transfermoney.storage

import sidomik.samples.transfermoney.exceptions.NegativeBalanceException
import sidomik.samples.transfermoney.exceptions.NonExistingAccountException
import sidomik.samples.transfermoney.model.Account
import java.math.BigDecimal
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

object AccountStorage {

    private val idSequence = AtomicLong()
    private val accounts = CopyOnWriteArrayList<Account>()

    fun create(account: Account): Account {
        if (account.balance < BigDecimal.ZERO) {
            throw NegativeBalanceException(account.balance)
        }

        account.id = idSequence.incrementAndGet()
        accounts.add(account)
        return account
    }

    fun find(id: Long): Account =
        accounts.find { a -> a.id == id } ?: throw NonExistingAccountException(id)
}