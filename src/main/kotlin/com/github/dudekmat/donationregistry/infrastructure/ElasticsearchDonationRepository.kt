package com.github.dudekmat.donationregistry.infrastructure

import co.elastic.clients.elasticsearch.ElasticsearchClient
import com.github.dudekmat.donationregistry.domain.CreatedDate
import com.github.dudekmat.donationregistry.domain.Donation
import com.github.dudekmat.donationregistry.domain.DonationDate
import com.github.dudekmat.donationregistry.domain.DonationId
import com.github.dudekmat.donationregistry.domain.DonationItem
import com.github.dudekmat.donationregistry.domain.DonationRepository
import com.github.dudekmat.donationregistry.domain.Donor
import com.github.dudekmat.donationregistry.domain.ModifiedDate
import com.github.dudekmat.donationregistry.infrastructure.ElasticsearchConfig.Companion.DONATION_INDEX
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository

private val log = KotlinLogging.logger {}

@Repository
class ElasticsearchDonationRepository(
    val elasticsearchClient: ElasticsearchClient
) : DonationRepository {

    override fun save(donation: Donation) {
        val elasticData = donation.mapFromDomain()
        try {
            elasticsearchClient.index { request ->
                request
                    .index(DONATION_INDEX)
                    .id(elasticData.id)
                    .document(elasticData)
            }
        } catch (ex: Exception) {
            log.error { "Error occurred indexing document: $donation, message: ${ex.message}" }
            ex.printStackTrace()
        }
    }

    override fun findById(donationId: DonationId): Donation? {
        val donationResponse = elasticsearchClient.get({ response ->
            response
                .index(DONATION_INDEX)
                .id(donationId.id)
        },
            ElasticDonationData::class.java
        )

        return if (donationResponse.found()) {
            donationResponse.source()?.mapToDomain()
        } else {
            null
        }
    }
}

private fun ElasticDonationData.mapToDomain() =
    Donation(
        donationId = DonationId(id),
        createdDate = CreatedDate(createdDate),
        modifiedDate = ModifiedDate(modifiedDate),
        donationDate = DonationDate(donationDate),
        donor = Donor(donor),
        items = items.map {
            DonationItem(
                type = it.type,
                details = it.details,
                unit = it.unit,
                quantity = it.quantity,
                price = it.price
            )
        }
    )


private fun Donation.mapFromDomain() =
    ElasticDonationData(
        id = donationId.id,
        createdDate = createdDate.dateTime,
        modifiedDate = modifiedDate.dateTime,
        donationDate = donationDate.dateTime,
        donor = donor.name,
        items = items.map {
            ElasticDonationItemData(
                type = it.type,
                details = it.details,
                unit = it.unit,
                quantity = it.quantity,
                price = it.price
            )
        }
    )
