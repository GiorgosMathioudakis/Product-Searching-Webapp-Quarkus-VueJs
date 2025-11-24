CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- indexes for search ILIKE %text% (GIN)
CREATE INDEX idx_product_name_search ON product USING GIN (name gin_trgm_ops);
CREATE INDEX idx_product_sku_search ON product USING GIN (sku gin_trgm_ops);

-- indexes for order by (b tree)
CREATE INDEX idx_product_price ON product(price);
CREATE INDEX idx_product_created_on ON product(created_on);
CREATE INDEX idx_product_updated_on ON product(updated_on);