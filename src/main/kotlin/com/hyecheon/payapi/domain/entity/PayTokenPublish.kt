package com.hyecheon.payapi.domain.entity

import java.math.BigInteger
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class PayTokenPublish(
		@Id
		@GeneratedValue
		var id: Long? = null,
		var usedToken: Boolean = false,
		var usedUserId: Long? = null,
		var money: BigInteger = BigInteger.ZERO
) : BaseEntity() {
	fun toResDto() {

	}
}