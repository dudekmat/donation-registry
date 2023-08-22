package com.github.dudekmat.donationregistry

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DonationRegistryApplication

fun main(args: Array<String>) {
    runApplication<DonationRegistryApplication>(*args)
}
