package sidomik.samples.transfermoney.storage

import sidomik.samples.transfermoney.exceptions.NegativeBalanceException
import sidomik.samples.transfermoney.exceptions.NonExistingAccountException
import sidomik.samples.transfermoney.model.Account
import sidomik.samples.transfermoney.model.AccountId
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object AccountStorage {

    private val idSequence = AtomicLong()
    private val accounts = ConcurrentHashMap<AccountId, Account>()

    fun create(account: Account): Account {
        if (account.balance < BigDecimal.ZERO) {
            throw NegativeBalanceException(account.balance)
        }

        account.accountId = AccountId(idSequence.incrementAndGet())
        accounts[account.accountId] = account
        return account
    }

    fun find(accountId: AccountId): Account {
        return accounts[accountId] ?: throw NonExistingAccountException(accountId)
    }
}