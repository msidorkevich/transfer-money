package sidomik.samples.transfermoney.model

data class Transfer(val from: Long,
                    val to: Long,
                    val amount: Double)