package com.hyecheon.payapi.service.impl

import com.hyecheon.payapi.domain.dto.SpreadReqDto
import com.hyecheon.payapi.domain.entity.PayToken
import com.hyecheon.payapi.domain.entity.PayTokenPublish
import com.hyecheon.payapi.domain.entity.UserRoomInfo
import com.hyecheon.payapi.repository.PayTokenPublishRepository
import com.hyecheon.payapi.repository.PayTokenRepository
import com.hyecheon.payapi.service.TokenService
import com.hyecheon.payapi.utils.shortUUID
import com.hyecheon.payapi.web.error.TokenException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDateTime

@Transactional
@Service
class TokenServiceImpl(
		val payTokenRepository: PayTokenRepository,
		val payTokenPublishRepository: PayTokenPublishRepository) : TokenService {
	val expiredMinute: Long = 10

	override fun saveToken(userRoomInfo: UserRoomInfo, spreadReqDto: SpreadReqDto): PayToken {
		val token = shortUUID()
		val payToken = PayToken(publishUserId = userRoomInfo.userId,
				peopleCount = spreadReqDto.peopleCount,
				money = spreadReqDto.money,
				publishRoomId = userRoomInfo.roomId,
				token = token)
		val moneys = spreadReqDto.money.divideAndRemainder(BigInteger.valueOf(spreadReqDto.peopleCount))
		val published = (1..spreadReqDto.peopleCount).map {
			PayTokenPublish(money = moneys[0])
		}
		published[0].money = published[0].money.plus(moneys[1])
		payToken.payTokenPublishes = published.toMutableList()
		return payTokenRepository.save(payToken)
	}

	override fun retrieveToken(userId: Long, token: String): PayToken {
		val payToken = payTokenRepository.findByToken(token) ?: throw TokenException("유효 하지 않은 token 입니다.")
		if (payToken.publishUserId != userId) {
			throw TokenException("토큰을 발행자가 아닙니다.")
		}
		if (payToken.createdDate!!.plusDays(7) < LocalDateTime.now()) {
			throw TokenException("토큰 조회는 7일 동안만 가능 합니다.")
		}
		return payToken
	}

	@Throws(TokenException::class)
	override fun getPaymentFromToken(userRoomInfo: UserRoomInfo, token: String): PayTokenPublish {
		val (userId, roomId) = userRoomInfo
		val payToken = payTokenRepository.findByToken(token) ?: throw TokenException("유효 하지 않은 token 입니다.")

		if (payToken.publishUserId == userId) {
			throw TokenException("유효 하지 않은 사용자 입니다.")
		}
		if (payToken.publishRoomId != roomId) {
			throw TokenException("유효 하지 않은 방 입니다.")
		}
		if (payToken.createdDate!!.plusMinutes(expiredMinute) < LocalDateTime.now()) {
			throw TokenException("유효 기간이 지난 토큰 입니다.")
		}

		val groupBy = payToken.payTokenPublishes.groupBy { it.usedToken }

		val usedPublishTokens = groupBy[true] ?: listOf()
		if (usedPublishTokens.any { it.usedUserId == userId }) {
			throw TokenException("1번만 받을 수 있습니다.")
		}

		val usedNotPublishTokens = groupBy[false] ?: listOf()
		if (usedNotPublishTokens.isEmpty()) {
			throw TokenException("모두 소진된 토큰 입니다.")
		}

		val payTokenPublish = usedNotPublishTokens[0]
		payTokenPublish.usedUserId = userId
		payTokenPublish.usedToken = true
		return payTokenPublishRepository.save(payTokenPublish)
	}


}