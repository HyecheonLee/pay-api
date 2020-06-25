package com.hyecheon.payapi.domain.dto

import com.hyecheon.payapi.domain.entity.PayToken
import java.math.BigInteger
import java.time.LocalDateTime

data class TokenReqDto(
		val token: String
)

data class TokenRetrieveDto(
		val publishedAt: LocalDateTime,
		val money: BigInteger,
		val publishedMoney: BigInteger,
		val publishedInfos: MutableSet<PublishedInfo>
) {
	companion object {
		fun fromEntity(payToken: PayToken): TokenRetrieveDto {

			val payTokenPublishedList = payToken.payTokenPublishes
					.filter { it.usedToken }
					.map { payTokenPublish -> PublishedInfo(userId = payTokenPublish.usedUserId!!, money = payTokenPublish.money) }
					.toMutableSet()

			val publishedMoney = payTokenPublishedList
					.map { payTokenPublish -> payTokenPublish.money }
					.reduce { acc, money -> acc.plus(money) }

			return TokenRetrieveDto(
					publishedAt = payToken.createdDate!!,
					money = payToken.money,
					publishedMoney = publishedMoney,
					publishedInfos = payTokenPublishedList)
		}
	}

	data class PublishedInfo(
			val userId: Long,
			val money: BigInteger
	) {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is PublishedInfo) return false

			if (userId != other.userId) return false
			if (money != other.money) return false

			return true
		}

		override fun hashCode(): Int {
			var result = userId.hashCode()
			result = 31 * result + money.hashCode()
			return result
		}
	}
}