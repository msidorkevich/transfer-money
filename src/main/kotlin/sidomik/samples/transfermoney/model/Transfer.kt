package sidomik.samples.transfermoney.model

import java.math.BigDecimal

data class Transfer(val from: Long,
                    val to: Long,
                    val amount: BigDecimal)