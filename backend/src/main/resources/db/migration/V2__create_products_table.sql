CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    brand VARCHAR(120),
    category VARCHAR(120),
    image_url VARCHAR(512),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO products (id, name, description, price, stock_quantity, brand, category, image_url)
VALUES
    (gen_random_uuid(), 'Solid Casual Shirt', 'Breathable cotton shirt for daily wear', 1299.00, 120, 'Roadster', 'Men', 'https://picsum.photos/seed/prod1/600/800'),
    (gen_random_uuid(), 'Printed Kurta Set', 'Elegant festive kurta with matching bottoms', 1599.00, 80, 'Libas', 'Women', 'https://picsum.photos/seed/prod2/600/800'),
    (gen_random_uuid(), 'Running Sneakers', 'Cushioned running shoes', 2499.00, 140, 'Puma', 'Footwear', 'https://picsum.photos/seed/prod3/600/800')
ON CONFLICT DO NOTHING;
