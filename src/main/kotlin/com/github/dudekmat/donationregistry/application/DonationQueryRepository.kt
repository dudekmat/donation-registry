package com.github.dudekmat.donationregistry.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.Instant

interface DonationQueryRepository {

    fun findById(id: String): DonationDetails

    fun findAll(pageable: Pageable): Page<DonationDetails>
}

data class DonationDetails(
    val id: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val donationDate: Instant,
    val donor: String,
    val items: List<DonationItemDetails> = listOf()
)

data class DonationItemDetails(
    val type: String,
    val details: String,
    val unit: String,
    val quantity: BigDecimal,
    val price: BigDecimal
)