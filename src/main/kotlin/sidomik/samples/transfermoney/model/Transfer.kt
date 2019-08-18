package sidomik.samples.transfermoney.model

import java.math.BigDecimal

data class Transfer(val from: AccountId,
                    val to: AccountId,
                    val amount: BigDecimal)