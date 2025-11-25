CREATE INDEX idx_experiment_composite
    ON product (updated_on DESC, name, sku);