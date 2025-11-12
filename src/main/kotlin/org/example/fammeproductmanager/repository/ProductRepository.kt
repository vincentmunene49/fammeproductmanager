package org.example.fammeproductmanager.repository

import org.example.fammeproductmanager.model.Product
import org.example.fammeproductmanager.model.ProductVariant
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
class ProductRepository(
    private val jdbcClient: JdbcClient
) {

    fun findAll(): List<Product> {
        val productsQuery = "SELECT * FROM products ORDER BY id"

        val products = jdbcClient.sql(productsQuery)
            .query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    externalId = rs.getLong("external_id"),
                    title = rs.getString("title"),
                    vendor = rs.getString("vendor"),
                    price = rs.getBigDecimal("price"),
                    createdAt = rs.getTimestamp("created_at")?.toLocalDateTime()
                )
            }
            .list()

        return products.map { product ->
            product.copy(variants = findVariantsByProductId(product.id!!))
        }
    }

    fun findById(id: Long): Product? {
        val sql = "SELECT * FROM products WHERE id = :id"

        val product = jdbcClient.sql(sql)
            .param("id", id)
            .query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    externalId = rs.getLong("external_id"),
                    title = rs.getString("title"),
                    vendor = rs.getString("vendor"),
                    price = rs.getBigDecimal("price"),
                    createdAt = rs.getTimestamp("created_at")?.toLocalDateTime()
                )
            }
            .optional()
            .orElse(null)

        return product?.copy(variants = findVariantsByProductId(id))
    }

    fun findByExternalId(externalId: Long): Product? {
        val sql = "SELECT id FROM products WHERE external_id = :externalId"

        val id = jdbcClient.sql(sql)
            .param("externalId", externalId)
            .query(Long::class.java)
            .optional()
            .orElse(null)

        return id?.let { findById(it) }
    }

    fun existsByExternalId(externalId: Long): Boolean {
        val sql = "SELECT COUNT(*) FROM products WHERE external_id = :externalId"

        val count = jdbcClient.sql(sql)
            .param("externalId", externalId)
            .query(Int::class.java)
            .single()

        return count > 0
    }

    @Transactional
    fun save(product: Product): Product {
        return if (product.id == null) {
            insert(product)
        } else {
            update(product)
        }
    }

    @Transactional
    fun deleteById(id: Long) {
        val sql = "DELETE FROM products WHERE id = :id"
        jdbcClient.sql(sql)
            .param("id", id)
            .update()
    }

    private fun insert(product: Product): Product {
        val sql = """
            INSERT INTO products (external_id, title, vendor, price, created_at)
            VALUES (:externalId, :title, :vendor, :price, :createdAt)
        """

        val keyHolder = GeneratedKeyHolder()

        jdbcClient.sql(sql)
            .param("externalId", product.externalId)
            .param("title", product.title)
            .param("vendor", product.vendor)
            .param("price", product.price)
            .param("createdAt", product.createdAt?.let { Timestamp.valueOf(it) })
            .update(keyHolder)

        val productId = keyHolder.keys?.get("id") as Long

        // Insert variants
        product.variants.forEach { variant ->
            insertVariant(variant.copy(productId = productId))
        }

        return findById(productId)!!
    }

    private fun update(product: Product): Product {
        val sql = """
            UPDATE products 
            SET title = :title, 
                vendor = :vendor,
                price = :price
            WHERE id = :id
        """

        jdbcClient.sql(sql)
            .param("id", product.id)
            .param("title", product.title)
            .param("vendor", product.vendor)
            .param("price", product.price)
            .update()

        // Delete and re-insert variants
        deleteVariantsByProductId(product.id!!)
        product.variants.forEach { variant ->
            insertVariant(variant.copy(productId = product.id))
        }

        return findById(product.id)!!
    }

    private fun findVariantsByProductId(productId: Long): List<ProductVariant> {
        val sql = "SELECT * FROM product_variants WHERE product_id = :productId"

        return jdbcClient.sql(sql)
            .param("productId", productId)
            .query { rs, _ ->
                ProductVariant(
                    id = rs.getLong("id"),
                    productId = rs.getLong("product_id"),
                    externalId = rs.getLong("external_id"),
                    title = rs.getString("title"),
                    sku = rs.getString("sku"),
                    price = rs.getBigDecimal("price")
                )
            }
            .list()
    }

    private fun insertVariant(variant: ProductVariant) {
        val sql = """
            INSERT INTO product_variants (product_id, external_id, title, sku, price)
            VALUES (:productId, :externalId, :title, :sku, :price)
        """

        jdbcClient.sql(sql)
            .param("productId", variant.productId)
            .param("externalId", variant.externalId)
            .param("title", variant.title)
            .param("sku", variant.sku)
            .param("price", variant.price)
            .update()
    }

    private fun deleteVariantsByProductId(productId: Long) {
        val sql = "DELETE FROM product_variants WHERE product_id = :productId"
        jdbcClient.sql(sql)
            .param("productId", productId)
            .update()
    }
}