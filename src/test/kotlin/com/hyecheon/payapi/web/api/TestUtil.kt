package com.hyecheon.payapi.web.api

import com.hyecheon.payapi.domain.dto.SpreadReqDto
import org.springframework.http.HttpHeaders
import java.math.BigInteger

fun createValidSpread(): SpreadReqDto {

	return SpreadReqDto(BigInteger.valueOf(10), 1)
}

fun createInValidSpread(): SpreadReqDto {
	return SpreadReqDto(BigInteger.valueOf(-1), -1)
}


fun createValidHeaders(): HttpHeaders {
	val headers = HttpHeaders()
	headers["Content-Type"] = "application/json"
	headers["X-USER-ID"] = "1"
	headers["X-ROOM-ID"] = "room-1"
	return headers
}

fun createValidClientHeaders(): HttpHeaders {
	val headers = HttpHeaders()
	headers["Content-Type"] = "application/json"
	headers["X-USER-ID"] = "2"
	headers["X-ROOM-ID"] = "room-1"
	return headers
}

fun createInValidHeaders(): HttpHeaders {
	val headers = HttpHeaders()
	headers["X-USER-ID"] = "userId"
	headers["X-ROOM-ID"] = "room-1"
	return headers
}