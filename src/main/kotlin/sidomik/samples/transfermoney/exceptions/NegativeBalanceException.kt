package sidomik.samples.transfermoney.exceptions

import java.lang.RuntimeException
import java.math.BigDecimal

class NegativeBalanceException(val balance: BigDecimal) : RuntimeException()