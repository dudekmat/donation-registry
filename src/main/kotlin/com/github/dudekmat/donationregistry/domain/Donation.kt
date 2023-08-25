package com.github.dudekmat.donationregistry.domain

import java.math.BigDecimal
import java.time.Instant

data class Donation(
        val donationId: DonationId,
        val createdDate: CreatedDate,
        val modifiedDate: ModifiedDate,
        val donationDate: DonationDate,
        val donor: Donor,
        val items: List<DonationItem> = listOf()
)

data class DonationId(val id: String)

data class CreatedDate(val dateTime: Instant)

data class ModifiedDate(val dateTime: Instant)

data class DonationDate(val dateTime: Instant)

data class Donor(val name: String)

data class DonationItem(
        val type: String,
        val details: String,
        val unit: String,
        val quantity: BigDecimal,
        val price: BigDecimal
)