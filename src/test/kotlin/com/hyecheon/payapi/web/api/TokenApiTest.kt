package com.hyecheon.payapi.web.api

import com.hyecheon.payapi.domain.dto.SpreadReqDto
import com.hyecheon.payapi.domain.entity.UserRoomInfo
import com.hyecheon.payapi.repository.PayTokenRepository
import com.hyecheon.payapi.service.TokenService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class TokenApiTest {

	@Autowired
	lateinit var testRestTemplate: TestRestTemplate

	@Autowired
	lateinit var payTokenRepository: PayTokenRepository

	@Autowired
	lateinit var paytokenPublishRepository: PayTokenRepository

	@Autowired
	lateinit var tokenService: TokenService
	lateinit var token: String

	companion object {
		val API_1_TOKEN = "/api/v1/tokens"
	}

	@BeforeEach
	fun cleanup() {
		payTokenRepository.deleteAll()
		paytokenPublishRepository.deleteAll()
		testRestTemplate.restTemplate.interceptors.clear()
	}

	@Test
	fun `토큰생성 X-USER-ID,X-ROOM-ID 없을 경우 400 에러`() {
		val createValidSpreadUser = createValidSpread()
		val postForEntity = testRestTemplate.postForEntity<Any>(API_1_TOKEN, createValidSpreadUser)
		assertThat(postForEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `토큰생성 X-USER-ID 숫자가 아닐 경우 400 에러`() {
		val validSpreadUser = createValidSpread()
		val invalidHeaders = createInValidHeaders()
		val httpEntity = HttpEntity(validSpreadUser, invalidHeaders)
		val postForEntity = testRestTemplate.exchange<Any>(API_1_TOKEN, HttpMethod.POST, httpEntity)
		assertThat(postForEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `토큰생성 201`() {
		val validSpreadUser = createValidSpread()
		val validHeader = createValidHeaders()
		val httpEntity = HttpEntity(validSpreadUser, validHeader)
		val postForEntity = testRestTemplate.exchange<Any>(API_1_TOKEN, HttpMethod.POST, httpEntity)
		assertThat(postForEntity.statusCode).isEqualTo(HttpStatus.CREATED)
		val body = postForEntity.body as LinkedHashMap<*, *>
		val data = body["data"] as LinkedHashMap<*, *>
		assertThat(data.containsKey("token")).isTrue()
		assertThat(data["token"]).isNotEqualTo("")
	}

	@Test
	fun `토큰생성시 파라미터가 없는 경우 400 에러`() {
		val invalidHeaders = createValidHeaders()
		val httpEntity = HttpEntity(null, invalidHeaders)
		val postForEntity = testRestTemplate.exchange<Any>(API_1_TOKEN, HttpMethod.POST, httpEntity)
		assertThat(postForEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `토큰생성시 파라미터값이 잘못된 경우 없는 경우 400 에러`() {
		val spread = createInValidSpread()
		val invalidHeaders = createValidHeaders()
		val httpEntity = HttpEntity(spread, invalidHeaders)
		val postForEntity = testRestTemplate.exchange<Any>(API_1_TOKEN, HttpMethod.POST, httpEntity)
		assertThat(postForEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `받기 요청 성공 200`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), createValidSpread())

		//발행 요청
		val validHeaders = createValidClientHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.PUT, httpEntity)

		//결과
		assertThat(exchange.statusCode).isEqualTo(HttpStatus.OK)
	}

	@Test
	fun `받기 요청 성공 금액 확인`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), createValidSpread())

		//발행 요청
		val validHeaders = createValidClientHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.PUT, httpEntity)

		//결과
		val data = getResponseEntityToData(exchange)
		assertThat(data.containsKey("money")).isTrue()
	}

	@Test
	fun `받기 요청 같은 사용자 2번 요청 오류`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))

		//발행 요청
		val validHeaders = createValidClientHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.PUT, httpEntity)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.PUT, httpEntity)

		//결과
		assertThat(exchange.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
		val data = getResponseEntityToMessage(exchange)
		assertThat(data).isEqualTo("1번만 받을 수 있습니다.")
	}

	@Test
	fun `받기 요청 뿌신 사용자가 요청 오류`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))

		//발행 요청
		val validHeaders = createValidHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.PUT, httpEntity)

		//결과
		assertThat(exchange.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
		val data = getResponseEntityToMessage(exchange)
		assertThat(data).isEqualTo("유효 하지 않은 사용자 입니다.")
	}

	@Test
	fun `받기 요청 동일한 대화방이 아닐경우 오류`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))

		//발행 요청
		val headers = HttpHeaders()
		headers["Content-Type"] = "application/json"
		headers["X-USER-ID"] = "2"
		headers["X-ROOM-ID"] = "room-2"
		val httpEntity = HttpEntity(null, headers)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.PUT, httpEntity)

		//결과
		assertThat(exchange.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
		val data = getResponseEntityToMessage(exchange)
		assertThat(data).isEqualTo("유효 하지 않은 방 입니다.")
	}

	@Test
	fun `받기 요청 10분이 지난 토큰 오류`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))
		saveToken.createdDate = LocalDateTime.now().minusMinutes(11)
		payTokenRepository.save(saveToken)

		//발행 요청
		val validHeaders = createValidClientHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.PUT, httpEntity)
		//결과
		assertThat(exchange.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
		val data = getResponseEntityToMessage(exchange)
		assertThat(data).isEqualTo("유효 기간이 지난 토큰 입니다.")
	}

	@Test
	fun `조회 요청`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))

		//조회 요청
		val validHeaders = createValidHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.GET, httpEntity)
		//결과
		assertThat(exchange.statusCode).isEqualTo(HttpStatus.OK)
	}

	@Test
	fun `조회 요청2`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))


		val clientUserId = 2
		val headers = HttpHeaders()
		headers["Content-Type"] = "application/json"
		headers["X-USER-ID"] = clientUserId.toString()
		headers["X-ROOM-ID"] = "room-1"

		//발행 요청
		val published = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.PUT, HttpEntity(null, headers))

		//조회 요청
		val validHeaders = createValidHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.GET, httpEntity)

		//결과
		val data = getResponseEntityToData(exchange)
		//발행 시간
		val publishedAt = LocalDateTime.parse(data["publishedAt"].toString())
		assertThat(publishedAt.year).isEqualTo(saveToken.createdDate!!.year)
		assertThat(publishedAt.month).isEqualTo(saveToken.createdDate!!.month)
		assertThat(publishedAt.dayOfWeek).isEqualTo(saveToken.createdDate!!.dayOfWeek)
		assertThat(publishedAt.hour).isEqualTo(saveToken.createdDate!!.hour)
		assertThat(publishedAt.minute).isEqualTo(saveToken.createdDate!!.minute)
		assertThat(publishedAt.second).isEqualTo(saveToken.createdDate!!.second)

		//발행 금액
		assertThat(data["money"]).isEqualTo(10)
		//받기 완료된 금액
		assertThat(data["publishedMoney"]).isEqualTo(5)

		//받기 완료된 정보
		val responseEntityToData = getResponseEntityToData(published)

		val publishedInfos = data["publishedInfos"] as ArrayList<LinkedHashMap<*, *>>
		val publishedInfo = publishedInfos[0]
		assertThat(publishedInfo["userId"]).isEqualTo(clientUserId)
		assertThat(publishedInfo["money"]).isEqualTo(responseEntityToData["money"])

	}

	@Test
	fun `조회 요청 뿌린 사람이랑 조회 사람이 다른 경우 오류`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))

		//조회 요청
		val validHeaders = createValidClientHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.GET, httpEntity)

		assertThat(exchange.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

		val message = getResponseEntityToMessage(exchange)
		assertThat(message).isEqualTo("토큰을 발행자가 아닙니다.")
	}

	@Test
	fun `조회 요청을 7일 지난 토큰에 대해서 요청 오류`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))
		saveToken.createdDate = LocalDateTime.now().minusDays(8)
		payTokenRepository.save(saveToken)

		//조회 요청
		val validHeaders = createValidHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.GET, httpEntity)

		//결과
		assertThat(exchange.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

		val message = getResponseEntityToMessage(exchange)
		assertThat(message).isEqualTo("토큰 조회는 7일 동안만 가능 합니다.")
	}

	@Test
	fun `생성된지 7일 지난 토큰 삭제`() {
		//7일 전 토큰 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))
		saveToken.createdDate = LocalDateTime.now().minusDays(8)
		payTokenRepository.save(saveToken)

		//삭제 요청
		tokenService.removePublishedTokenByLocalDateTime()

		//결과
		val findAll = payTokenRepository.findAll()
		assertThat(findAll.size).isEqualTo(0)
	}

	@Test
	fun `조회 요청을 7일 지난 토큰에 대해서 요청2 오류`() {
		//user token 생성
		val saveToken = tokenService.saveToken(UserRoomInfo(1, "room-1"), SpreadReqDto(10, 2))
		saveToken.createdDate = LocalDateTime.now().minusDays(8)
		payTokenRepository.save(saveToken)
		//삭제 요청
		tokenService.removePublishedTokenByLocalDateTime()

		//조회 요청
		val validHeaders = createValidHeaders()
		val httpEntity = HttpEntity(null, validHeaders)
		val exchange = testRestTemplate.exchange<Any>("$API_1_TOKEN/${saveToken.token}", HttpMethod.GET, httpEntity)

		//결과
		assertThat(exchange.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

		val message = getResponseEntityToMessage(exchange)
		assertThat(message).isEqualTo("유효 하지 않은 token 입니다.")
	}


	private fun getResponseEntityToData(responseEntity: ResponseEntity<Any>): LinkedHashMap<*, *> {
		val body = responseEntity.body as LinkedHashMap<*, *>
		return body["data"] as LinkedHashMap<*, *>
	}

	private fun getResponseEntityToMessage(responseEntity: ResponseEntity<Any>): String {
		val body = responseEntity.body as LinkedHashMap<*, *>
		return body["message"] as String
	}
}