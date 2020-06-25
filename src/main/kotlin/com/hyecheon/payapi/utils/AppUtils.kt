package com.hyecheon.payapi.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

inline fun seoulDateTime(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

inline fun getTimeNow(pattern: String = "yyyy-MM-dd HH:mm:ss"): String = seoulDateTime().format(DateTimeFormatter.ofPattern(pattern))
fun LocalDateTime.getString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String = format(DateTimeFormatter.ofPattern(pattern))

fun shortUUID(): String {
	val uuid = UUID.randomUUID()
	val l = ByteBuffer.wrap(uuid.toString().toByteArray()).long
	return l.toString(Character.MAX_RADIX)
}

interface Log {
	val log: Logger get() = LoggerFactory.getLogger(this.javaClass)
}
