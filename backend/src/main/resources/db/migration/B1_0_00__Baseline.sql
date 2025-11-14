CREATE TABLE product
(

    id BIGINT PRIMARY KEY,
    price NUMERIC(12,2) NOT NULL,
    sku VARCHAR(12) NOT NULL,
    description VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    created_on TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_on TIMESTAMP WITH TIME ZONE DEFAULT now()

);