package com.github.dudekmat.donationregistry.infrastructure

import com.github.dudekmat.donationregistry.application.DonationDetails
import com.github.dudekmat.donationregistry.application.DonationItemDetails
import com.github.dudekmat.donationregistry.application.DonationQueryRepository
import com.github.dudekmat.donationregistry.shared.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
class ElasticsearchDonationQueryRepository(
    private val donationRepository: SpringDataElasticsearchDonationRepository
) : DonationQueryRepository {

    override fun findById(id: String): DonationDetails {
        return donationRepository.findById(id)
            .map { donation -> donation.mapToDonationDetails() }
            .orElseThrow { NotFoundException("Donation not found with id=${id}") }
    }

    override fun findAll(pageable: Pageable): Page<DonationDetails> {
        return donationRepository.findAll(pageable)
            .map { donation -> donation.mapToDonationDetails() }
    }
}

interface SpringDataElasticsearchDonationRepository : ElasticsearchRepository<ElasticDonationData, String>

private fun ElasticDonationData.mapToDonationDetails() =
    DonationDetails(
        id = id,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        donationDate = donationDate,
        donor = donor,
        items = items.map {
            DonationItemDetails(
                type = it.type,
                details = it.details,
                unit = it.unit,
                quantity = it.quantity,
                price = it.price
            )
        }
    )

