package sidomik.samples.transfermoney.exceptions

import java.lang.RuntimeException

class NonExistingAccountException(val id: Long) : RuntimeException()