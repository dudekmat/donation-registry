package com.github.dudekmat.donationregistry.api

import com.github.dudekmat.donationregistry.application.DonationData
import com.github.dudekmat.donationregistry.application.DonationDataItem
import com.github.dudekmat.donationregistry.application.DonationDetails
import com.github.dudekmat.donationregistry.application.DonationQueryRepository
import com.github.dudekmat.donationregistry.application.DonationService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.Instant

@RestController
@Validated
@RequestMapping("/api/donations")
class DonationController(
    private val donationService: DonationService,
    private val donationQueryRepository: DonationQueryRepository) {

    @PostMapping
    fun createDonation(@RequestBody @Valid donationPayload: DonationPayload) {
        donationService.create(donationPayload.toDonationData())
    }

    @PutMapping("/{id}")
    fun updateDonation(@PathVariable @NotBlank id: String,
                       @RequestBody @Valid donationPayload: DonationPayload) {
        donationService.update(id, donationPayload.toDonationData())
    }

    @GetMapping("/{id}")
    fun getDonationById(@PathVariable @NotBlank id: String): DonationDetails {
        return donationQueryRepository.findById(id);
    }

    @GetMapping
    fun getDonations(@PageableDefault(size = 10, page = 0) pageable: Pageable): Page<DonationDetails> {
        return donationQueryRepository.findAll(pageable)
    }

}

data class DonationPayload(
    @field:NotNull val donationDate: Instant? = null,
    @field:NotBlank val donor: String? = null,
    @field:NotNull
    @field:NotEmpty
    @field:Valid
    val items: List<DonationItemPayload>? = listOf()
) {
    fun toDonationData() =
        DonationData(
            donationDate = donationDate!!,
            donor = donor!!,
            items = items!!.map {
                DonationDataItem(
                    type = it.type!!,
                    details = it.details!!,
                    unit = it.unit!!,
                    quantity = it.quantity!!,
                    price = it.price!!
                )
            }
        )
}

data class DonationItemPayload(
    @field:NotBlank val type: String? = null,
    @field:NotBlank val details: String? = null,
    @field:NotBlank val unit: String? = null,
    @field:NotNull @field:Positive val quantity: BigDecimal? = null,
    @field:NotNull @field:Positive val price: BigDecimal? = null
)
