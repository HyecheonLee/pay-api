package com.hyecheon.payapi.domain.dto

import java.math.BigInteger
import javax.validation.constraints.Min

data class SpreadReqDto(
		@Min(value = 1, message = "금액은 1원 이상을 보내야 합니다.")
		var money: BigInteger,
		@Min(value = 1, message = "인원 수 는 1명 이상 이어야 합니다.")
		var peopleCount: Long
)