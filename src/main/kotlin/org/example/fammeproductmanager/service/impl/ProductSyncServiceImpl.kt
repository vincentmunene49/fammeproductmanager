package org.example.fammeproductmanager.service.impl

import org.example.fammeproductmanager.dto.ProductsListResponse
import org.example.fammeproductmanager.mapper.ProductMapper
import org.example.fammeproductmanager.repository.ProductRepository
import org.example.fammeproductmanager.service.ProductSyncService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Service
class ProductSyncServiceImpl (
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper,
    private val restTemplate: RestTemplate = RestTemplate()
): ProductSyncService {

    private val logger = LoggerFactory.getLogger(ProductSyncServiceImpl::class.java)

    companion object {
        private const val API_URL = "https://famme.no/products.json"
        private const val MAX_PRODUCTS = 50
    }


    @Scheduled(fixedRate = 3600000, initialDelay = 0)
    @Transactional
    override fun syncProducts() {
        logger.info("Starting product sync from external API...")

        try {
            // Fetch from external API
            val response = restTemplate.getForObject(API_URL, ProductsListResponse::class.java)

            if (response?.products == null) {
                logger.error("No products received from API")
                return
            }

            val apiProducts = response.products.take(MAX_PRODUCTS)

            var savedCount = 0
            var updatedCount = 0

            // Process each product
            apiProducts.forEach { apiProduct ->
                try {
                    if (productRepository.existsByExternalId(apiProduct.id)) {
                        // Product exists - update it
                        val existingProduct = productRepository.findByExternalId(apiProduct.id)!!
                        val updatedProduct = productMapper.fromApiResponse(apiProduct)
                            .copy(id = existingProduct.id)
                        productRepository.save(updatedProduct)
                        updatedCount++
                    } else {
                        // New product - insert it
                        val newProduct = productMapper.fromApiResponse(apiProduct)
                        productRepository.save(newProduct)
                        savedCount++
                    }
                } catch (e: Exception) {
                    logger.error("Error syncing product ${apiProduct.id}: ${e.message}")
                }
            }

            logger.info("Product sync completed. Saved: $savedCount, Updated: $updatedCount")

        } catch (e: Exception) {
            logger.error("Error syncing products: ", e)
        }
    }


    override fun triggerSync() {
        syncProducts()
    }
}