package com.hyecheon.payapi.service

import com.hyecheon.payapi.domain.dto.SpreadReqDto
import com.hyecheon.payapi.domain.entity.PayToken
import com.hyecheon.payapi.domain.entity.PayTokenPublish
import com.hyecheon.payapi.domain.entity.UserRoomInfo

interface TokenService {
	fun getPaymentFromToken(userRoomInfo: UserRoomInfo, token: String): PayTokenPublish
	fun saveToken(userRoomInfo: UserRoomInfo, spreadReqDto: SpreadReqDto): PayToken
	fun retrieveToken(userId: Long, token: String): PayToken
}