CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        external_id BIGINT UNIQUE NOT NULL,
                                        title VARCHAR(500) NOT NULL,
    vendor VARCHAR(255),
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );


CREATE TABLE IF NOT EXISTS product_variants (
                                                id BIGSERIAL PRIMARY KEY,
                                                product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    external_id BIGINT NOT NULL,
    title VARCHAR(255),
    sku VARCHAR(255),
    price DECIMAL(10, 2)
    );

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_products_external_id ON products(external_id);
CREATE INDEX IF NOT EXISTS idx_variants_product_id ON product_variants(product_id);