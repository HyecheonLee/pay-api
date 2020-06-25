package com.hyecheon.payapi.web.error

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.error.ErrorAttributeOptions.Include
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest

@RestController
class ErrorHandler(
		val errorAttributes: ErrorAttributes) : ErrorController {
	@RequestMapping("/error")
	fun handleError(webRequest: WebRequest): ApiError {
		val attributes = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(Include.MESSAGE))
		val message = attributes["message"] ?: ""
		val error = attributes["error"] ?: ""
		val url = attributes["path"] ?: ""
		val status = attributes["status"] ?: 400
		return ApiRuntimeErrors(status = status as Int, message = message as String, error = error as String, url = url as String)
	}

	override fun getErrorPath() = "/error"
}