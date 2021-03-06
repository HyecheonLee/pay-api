package com.hyecheon.payapi.web.api

import com.hyecheon.payapi.domain.dto.SpreadReqDto
import com.hyecheon.payapi.domain.dto.TokenRetrieveDto
import com.hyecheon.payapi.domain.entity.UserRoomInfo
import com.hyecheon.payapi.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/tokens")
class TokenApi(private val tokenService: TokenService) {

	@PostMapping(value = ["/", ""])
	fun spreadReq(@RequestHeader("X-USER-ID") userId: Long,
	              @RequestHeader("X-ROOM-ID") roomId: String,
	              @Valid @RequestBody spreadRequestDto: SpreadReqDto,
	              request: HttpServletRequest) = let {
		val payToken = tokenService.saveToken(UserRoomInfo(userId, roomId), spreadRequestDto)
		ResponseEntity.status(HttpStatus.CREATED).body(ApiMessage(status = HttpStatus.CREATED.value(), data = mapOf("token" to payToken.token), url = request.servletPath))
	}

	@PutMapping("/{token}")
	fun tokenReceive(@RequestHeader("X-USER-ID") userId: Long,
	                 @RequestHeader("X-ROOM-ID") roomId: String,
	                 @PathVariable token: String,
	                 request: HttpServletRequest) = let {
		val paymentFromToken = tokenService.getPaymentFromToken(UserRoomInfo(userId, roomId), token)
		ApiMessage(status = HttpStatus.OK.value(), data = mapOf("money" to paymentFromToken.money), url = request.servletPath)
	}

	@GetMapping("/{token}")
	fun retrieveToken(@RequestHeader("X-USER-ID") userId: Long,
	                  @PathVariable token: String,
	                  request: HttpServletRequest): ApiMessage {

		val retrieveToken = tokenService.retrieveToken(userId, token)
		val retrieveDto = TokenRetrieveDto.fromEntity(retrieveToken)
		return ApiMessage(status = HttpStatus.OK.value(), data = retrieveDto, url = request.servletPath)
	}
}