package com.hyecheon.payapi.repository

import com.hyecheon.payapi.domain.entity.PayTokenPublish
import org.springframework.data.jpa.repository.JpaRepository

interface PayTokenPublishRepository : JpaRepository<PayTokenPublish, Long> {
}