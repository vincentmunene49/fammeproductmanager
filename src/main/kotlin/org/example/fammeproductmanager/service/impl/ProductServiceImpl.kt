package org.example.fammeproductmanager.service.impl

import org.example.fammeproductmanager.model.Product
import org.example.fammeproductmanager.repository.ProductRepository
import org.example.fammeproductmanager.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository

): ProductService{
    override fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    override fun getProductById(id: Long): Product? {
        return productRepository.findById(id)
    }

    @Transactional
    override fun createProduct(product: Product): Product {
        val newProduct = product.copy(
            createdAt = LocalDateTime.now()
        )
        return productRepository.save(newProduct)
    }

    @Transactional
    override fun deleteProduct(id: Long) {
        productRepository.deleteById(id)
    }
}