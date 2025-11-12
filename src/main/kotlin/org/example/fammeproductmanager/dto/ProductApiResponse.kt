package org.example.fammeproductmanager.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductApiResponse(
    val id: Long,
    val title: String,
    val vendor: String?,

    @JsonProperty("product_type")
    val productType: String?,

    val variants: List<VariantApiDto>?
)

data class VariantApiDto(
    val id: Long,
    val title: String?,
    val sku: String?,
    val price: String?
)
data class ProductsListResponse(
    val products: List<ProductApiResponse>
)