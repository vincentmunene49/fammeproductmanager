package org.example.fammeproductmanager.controller

import org.example.fammeproductmanager.model.Product
import org.example.fammeproductmanager.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal
@Controller
class WebController(
    private val productService: ProductService

) {
    /**
     * Main page - shows the UI
     */
    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    /**
     * HTMX endpoint - Load products into table
     * Returns HTML fragment (not full page)
     */
    @GetMapping("/products/load")
    fun loadProducts(model: Model): String {
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "fragments/product-table :: products"
    }

    /**
     * HTMX endpoint - Add new product
     * Returns updated table HTML fragment
     */
    @PostMapping("/products/add")
    fun addProduct(
        @RequestParam title: String,
        @RequestParam(required = false) vendor: String?,
        @RequestParam(required = false) price: BigDecimal?,
        model: Model
    ): String {
        // Create new product
        val newProduct = Product(
            externalId = System.currentTimeMillis(), // Use timestamp as external ID
            title = title,
            vendor = vendor,
            price = price,
            variants = emptyList()
        )

        // Save to database
        productService.createProduct(newProduct)

        // Return updated product list
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "fragments/product-table :: products"
    }
}