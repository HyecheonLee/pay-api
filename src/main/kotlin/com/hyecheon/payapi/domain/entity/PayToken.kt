package com.hyecheon.payapi.domain.entity

import java.math.BigInteger
import javax.persistence.*

@Entity
data class PayToken(
		@Id
		@GeneratedValue
		var id: Long? = null,
		val token: String,
		val money: BigInteger = BigInteger.ZERO,
		val peopleCount: Long = 0,
		val publishUserId: Long,
		val publishRoomId: String,
		@OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
		@JoinColumn(name = "pay_token_id")
		var payTokenPublishes: MutableList<PayTokenPublish> = mutableListOf()
) : BaseEntity()