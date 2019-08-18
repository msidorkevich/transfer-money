package sidomik.samples.transfermoney.exceptions

import sidomik.samples.transfermoney.model.AccountId
import java.lang.RuntimeException
import java.math.BigDecimal

class NotEnoughMoneyException(val accountId: AccountId,
                              val balance: BigDecimal,
                              val amount: BigDecimal) : RuntimeException()