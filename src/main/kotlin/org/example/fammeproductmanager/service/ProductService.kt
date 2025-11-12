package org.example.fammeproductmanager.service

import org.example.fammeproductmanager.model.Product

interface ProductService {
    fun getAllProducts(): List<Product>
    fun getProductById(id: Long): Product?
    fun createProduct(product: Product): Product
    fun deleteProduct(id: Long)
}