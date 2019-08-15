package sidomik.samples.transfermoney.storage

import sidomik.samples.transfermoney.model.Account
import java.lang.IllegalArgumentException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

object AccountStorage {

    private val idSequence = AtomicLong()
    private val accounts = CopyOnWriteArrayList<Account>()

    fun create(account: Account): Account {
        account.id = idSequence.incrementAndGet()
        accounts.add(account)
        return account
    }

    fun find(id: Long): Account =
        accounts.find { a -> a.id == id } ?: throw IllegalArgumentException("No account with id=$id found")
}