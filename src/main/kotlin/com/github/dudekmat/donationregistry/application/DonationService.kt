package com.github.dudekmat.donationregistry.application

import com.github.dudekmat.donationregistry.domain.CreatedDate
import com.github.dudekmat.donationregistry.domain.Donation
import com.github.dudekmat.donationregistry.domain.DonationDate
import com.github.dudekmat.donationregistry.domain.DonationId
import com.github.dudekmat.donationregistry.domain.DonationItem
import com.github.dudekmat.donationregistry.domain.DonationRepository
import com.github.dudekmat.donationregistry.domain.Donor
import com.github.dudekmat.donationregistry.domain.ModifiedDate
import com.github.dudekmat.donationregistry.shared.NotFoundException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class DonationService(
    private val donationRepository: DonationRepository
) {

    fun create(newDonationData: DonationData) {
        val donation = Donation(
            donationId = DonationId(UUID.randomUUID().toString()),
            createdDate = CreatedDate(Instant.now()),
            modifiedDate = ModifiedDate(Instant.now()),
            donationDate = DonationDate(newDonationData.donationDate),
            donor = Donor(newDonationData.donor),
            items = newDonationData.items.map {
                DonationItem(
                    type = it.type,
                    details = it.details,
                    unit = it.unit,
                    quantity = it.quantity,
                    price = it.price
                )
            }
        )

        donationRepository.save(donation)
    }

    fun update(id: String, donationData: DonationData) {
        val foundDonation = donationRepository.findById(DonationId(id))
            ?: throw NotFoundException("Donation not found with id=$id")

        val donation = Donation(
            donationId = DonationId(id),
            createdDate = foundDonation.createdDate,
            modifiedDate = ModifiedDate(Instant.now()),
            donationDate = DonationDate(donationData.donationDate),
            donor = Donor(donationData.donor),
            items = donationData.items.map {
                DonationItem(
                    type = it.type,
                    details = it.details,
                    unit = it.unit,
                    quantity = it.quantity,
                    price = it.price
                )
            }
        )

        donationRepository.save(donation)
    }
}

data class DonationData(
    val donationDate: Instant,
    val donor: String,
    val items: List<DonationDataItem> = listOf()
)

data class DonationDataItem(
    val type: String,
    val details: String,
    val unit: String,
    val quantity: BigDecimal,
    val price: BigDecimal
)