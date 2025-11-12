package org.example.fammeproductmanager.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Product(
    val id: Long? = null,
    val externalId: Long,
    val title: String,
    val vendor: String? = null,
    val price: BigDecimal? = null,
    val createdAt: LocalDateTime? = null,
    val variants: List<ProductVariant> = emptyList()
)