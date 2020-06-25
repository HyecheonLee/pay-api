package com.hyecheon.payapi.web.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.hyecheon.payapi.utils.getTimeNow

@JsonInclude(value = JsonInclude.Include.NON_NULL)
data class ApiMessage(
		var status: Int,
		var url: String = "",
		var data: Any?,
		val timestamp: String = getTimeNow()
)