package sidomik.samples.transfermoney.model

data class Transfer(val from: Long,
                    val to: Long,
                    val currency: String,
                    val amount: Double)