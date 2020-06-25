package com.hyecheon.payapi.repository

import com.hyecheon.payapi.domain.entity.PayToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PayTokenRepository : JpaRepository<PayToken, Long> {
	@Query(value = "select t from PayToken t join fetch t.payTokenPublishes p where p.usedToken=false")
	fun findByTokenNotUsed(token: String): PayToken?

	@Query(value = "select t from PayToken t join fetch t.payTokenPublishes p where t.token=:token")
	fun findByToken(token: String): PayToken?

}