package org.example.fammeproductmanager.controller

import org.example.fammeproductmanager.model.Product
import org.example.fammeproductmanager.service.ProductService
import org.example.fammeproductmanager.service.ProductSyncService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService,
    private val productSyncService: ProductSyncService
) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<Product>> {
        val products = productService.getAllProducts()
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<Product> {
        val product = productService.getProductById(id)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    fun createProduct(@RequestBody product: Product): ResponseEntity<Product> {
        val createdProduct = productService.createProduct(product)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct)
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/sync")
    fun triggerSync(): ResponseEntity<Map<String, String>> {
        productSyncService.triggerSync()
        return ResponseEntity.ok(mapOf(
            "message" to "Product sync triggered successfully",
            "status" to "success"
        ))
    }
}