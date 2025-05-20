-- Insert roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_MODERATOR');

-- Insert test users (password is 'admin123' encoded with bcrypt)
INSERT INTO users (username, email, password, first_name, last_name, enabled) 
VALUES ('admin', 'admin@example.com', '$2a$10$ixlPY3AAd4ty1l6E2IsXR.OGJi1hLqKi6WXuVVTnrI.4wdEz8c5Tq', 'Admin', 'User', true);

INSERT INTO users (username, email, password, first_name, last_name, enabled) 
VALUES ('user', 'user@example.com', '$2a$10$ixlPY3AAd4ty1l6E2IsXR.OGJi1hLqKi6WXuVVTnrI.4wdEz8c5Tq', 'Regular', 'User', true);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2); -- admin has ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1); -- user has ROLE_USER

-- Insert test products
INSERT INTO products (name, description, price, quantity, image_url)
VALUES ('Test Product 1', 'This is a test product 1', 99.99, 100, 'https://example.com/image1.jpg');

INSERT INTO products (name, description, price, quantity, image_url)
VALUES ('Test Product 2', 'This is a test product 2', 149.99, 50, 'https://example.com/image2.jpg');

INSERT INTO products (name, description, price, quantity, image_url)
VALUES ('Test Product 3', 'This is a test product 3', 199.99, 25, 'https://example.com/image3.jpg');

-- Insert test categories
INSERT INTO categories (name, description)
VALUES ('Electronics', 'Electronic devices and accessories');

INSERT INTO categories (name, description)
VALUES ('Clothing', 'Clothes and fashion items');

-- Map products to categories
INSERT INTO product_categories (product_id, category_id)
VALUES (1, 1); -- Product 1 belongs to Electronics

INSERT INTO product_categories (product_id, category_id)
VALUES (2, 1); -- Product 2 belongs to Electronics

INSERT INTO product_categories (product_id, category_id)
VALUES (3, 2); -- Product 3 belongs to Clothing

-- Create test carts for users
INSERT INTO carts (user_id, created_at, updated_at)
VALUES (1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO carts (user_id, created_at, updated_at)
VALUES (2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Add items to carts
INSERT INTO cart_items (cart_id, product_id, quantity)
VALUES (1, 1, 2); -- Admin's cart has 2 of product 1

INSERT INTO cart_items (cart_id, product_id, quantity)
VALUES (2, 2, 1); -- User's cart has 1 of product 2 