package com.hyecheon.payapi.domain.dto

import java.math.BigInteger

data class PayTokenPublishDto(
		var usedUserId: Long,
		val money: BigInteger = BigInteger.ZERO
)