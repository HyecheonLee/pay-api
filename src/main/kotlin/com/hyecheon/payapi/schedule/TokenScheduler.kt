package com.hyecheon.payapi.schedule

import com.hyecheon.payapi.service.TokenService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TokenScheduler(
		val tokenService: TokenService) {

	@Scheduled(cron = "0  0  2  *  *  *")
	fun tokenRemoveScheduler() {
		tokenService.removePublishedTokenByLocalDateTime(LocalDateTime.now().minusDays(7))
	}
}