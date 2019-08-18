package sidomik.samples.transfermoney.exceptions

import sidomik.samples.transfermoney.model.AccountId
import java.lang.RuntimeException

class NonExistingAccountException(val accountId: AccountId) : RuntimeException()