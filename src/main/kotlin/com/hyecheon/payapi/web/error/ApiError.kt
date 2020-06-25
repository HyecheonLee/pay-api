package com.hyecheon.payapi.web.error

import com.fasterxml.jackson.annotation.JsonInclude
import com.hyecheon.payapi.utils.getTimeNow

interface ApiError

@JsonInclude(value = JsonInclude.Include.NON_NULL)
data class ApiValidationErrors(
		var status: Int,
		var error: String = "",
		var url: String = "",
		var validationErrors: Map<String, String> = mapOf(),
		val timestamp: String = getTimeNow()) : ApiError {
}

@JsonInclude(value = JsonInclude.Include.NON_NULL)
data class ApiRuntimeErrors(
		var status: Int,
		var error: String = "",
		var url: String = "",
		var message: String = "",
		val timestamp: String = getTimeNow()) : ApiError