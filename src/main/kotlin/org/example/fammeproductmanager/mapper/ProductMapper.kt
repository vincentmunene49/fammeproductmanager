package org.example.fammeproductmanager.mapper

import org.example.fammeproductmanager.dto.ProductApiResponse
import org.example.fammeproductmanager.dto.VariantApiDto
import org.example.fammeproductmanager.model.Product
import org.example.fammeproductmanager.model.ProductVariant
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class ProductMapper {
    fun fromApiResponse(apiProduct: ProductApiResponse): Product {
        val variants = apiProduct.variants?.map { variantDto ->
            fromVariantApiDto(variantDto)
        } ?: emptyList()
        val productPrice = variants.firstOrNull()?.price
        return Product(
            externalId = apiProduct.id,
            title = apiProduct.title,
            vendor = apiProduct.vendor,
            price = productPrice,
            createdAt = LocalDateTime.now(),
            variants = variants
        )
    }

    private fun fromVariantApiDto(dto: VariantApiDto): ProductVariant {
        return ProductVariant(
            externalId = dto.id,
            title = dto.title,
            sku = dto.sku,
            price = dto.price?.let { parsePrice(it) }

        )

    }

    private fun parsePrice(priceStr: String): BigDecimal? {
        return try {
            BigDecimal(priceStr)
        } catch (e: Exception) {
            null
        }
    }
}