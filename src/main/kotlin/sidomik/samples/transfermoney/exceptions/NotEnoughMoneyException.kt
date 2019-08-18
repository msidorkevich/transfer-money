package sidomik.samples.transfermoney.exceptions

import java.lang.RuntimeException
import java.math.BigDecimal

class NotEnoughMoneyException(val id: Long,
                              val balance: BigDecimal,
                              val amount: BigDecimal) : RuntimeException()