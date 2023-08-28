package com.github.dudekmat.donationregistry.infrastructure

import com.github.dudekmat.donationregistry.infrastructure.ElasticsearchConfig.Companion.DONATION_INDEX
import org.springframework.data.elasticsearch.annotations.Document
import java.math.BigDecimal
import java.time.Instant

@Document(indexName = DONATION_INDEX)
data class ElasticDonationData(
    val id: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val donationDate: Instant,
    val donor: String,
    val items: List<ElasticDonationItemData> = listOf()
)

data class ElasticDonationItemData(
    val type: String,
    val details: String,
    val unit: String,
    val quantity: BigDecimal,
    val price: BigDecimal
)