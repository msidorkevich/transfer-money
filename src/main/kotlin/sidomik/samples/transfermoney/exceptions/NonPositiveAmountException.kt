package sidomik.samples.transfermoney.exceptions

import java.lang.RuntimeException
import java.math.BigDecimal

class NonPositiveAmountException(val amount: BigDecimal) : RuntimeException()