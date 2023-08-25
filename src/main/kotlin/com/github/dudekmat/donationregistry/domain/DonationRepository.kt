package com.github.dudekmat.donationregistry.domain

interface DonationRepository {

    fun save(donation: Donation)

    fun findById(donationId: DonationId): Donation?
}