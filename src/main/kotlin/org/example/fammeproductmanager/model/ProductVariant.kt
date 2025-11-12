package org.example.fammeproductmanager.model

import java.math.BigDecimal

data class ProductVariant(
    val id: Long? = null,
    val productId: Long? = null,
    val externalId: Long,
    val title: String? = null,
    val sku: String? = null,
    val price: BigDecimal? = null
)