-- V1: Initial database schema for QtiFood application
-- Creates all tables, indexes, constraints and foreign keys

-- ============================================
-- 1. USERS TABLE
-- ============================================
CREATE TABLE users (
    firebase_user_id VARCHAR(128) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    date_of_birth DATE,
    gender VARCHAR(10),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 2. ROLES TABLE
-- ============================================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- ============================================
-- 3. USER_ROLES JOIN TABLE
-- ============================================
CREATE TABLE user_roles (
    user_id VARCHAR(128) NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================
-- 4. STORES TABLE
-- ============================================
CREATE TABLE stores (
    id BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(128) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(255),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    phone VARCHAR(20),
    email VARCHAR(100),
    image_url TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    op_status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    open_time TIME,
    close_time TIME,
    view_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE
);

CREATE INDEX idx_stores_owner_id ON stores(owner_id);
CREATE INDEX idx_stores_status ON stores(status);

-- ============================================
-- 5. CATEGORIES TABLE
-- ============================================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 6. STORE_CATEGORIES TABLE
-- ============================================
CREATE TABLE store_categories (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_store_categories_store_id ON store_categories(store_id);
CREATE INDEX idx_store_categories_category_id ON store_categories(category_id);

-- ============================================
-- 7. PRODUCTS TABLE
-- ============================================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    store_category_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(12,2) NOT NULL,
    discount_price DECIMAL(12,2),
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    admin_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (store_category_id) REFERENCES store_categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_products_store_id ON products(store_id);
CREATE INDEX idx_products_store_category_id ON products(store_category_id);
CREATE INDEX idx_products_status ON products(status);

-- ============================================
-- 8. PRODUCT_IMAGES TABLE
-- ============================================
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_product_images_is_primary ON product_images(is_primary);

-- ============================================
-- 9. ADDRESSES TABLE
-- ============================================
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    receiver VARCHAR(100) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    address VARCHAR(255) NOT NULL,
    lat DECIMAL(9,6),
    log DECIMAL(9,6),
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user_id ON addresses(user_id);

-- ============================================
-- 10. DRIVERS TABLE
-- ============================================
CREATE TABLE drivers (
    id VARCHAR(128) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    avatar_url TEXT,
    date_of_birth DATE,
    address VARCHAR(255),
    vehicle_type VARCHAR(50),
    vehicle_plate VARCHAR(20) UNIQUE,
    vehicle_plate_image_url TEXT,
    vehicle_registration_image_url TEXT,
    cccd_number VARCHAR(20),
    cccd_front_image_url TEXT,
    cccd_back_image_url TEXT,
    license_number VARCHAR(50),
    license_image_url TEXT,
    verified BOOLEAN NOT NULL DEFAULT false,
    verification_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_drivers_phone ON drivers(phone);
CREATE INDEX idx_drivers_verification_status ON drivers(verification_status);
CREATE INDEX idx_drivers_verified ON drivers(verified);
CREATE INDEX idx_drivers_status ON drivers(status);

-- ============================================
-- 11. ORDERS TABLE
-- ============================================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(128),
    store_id BIGINT,
    driver_id VARCHAR(128),
    shipping_address_id BIGINT,
    total_amount DECIMAL(12,2),
    shipping_fee DECIMAL(12,2),
    admin_voucher_id BIGINT,
    seller_voucher_id BIGINT,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    paid_at TIMESTAMP,
    order_status VARCHAR(50) NOT NULL,
    note TEXT,
    cancel_reason TEXT,
    expected_delivery_time TIMESTAMP,
    rating_status BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE SET NULL,
    FOREIGN KEY (shipping_address_id) REFERENCES addresses(id) ON DELETE SET NULL
);

CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_store_id ON orders(store_id);
CREATE INDEX idx_orders_driver_id ON orders(driver_id);
CREATE INDEX idx_orders_order_status ON orders(order_status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- ============================================
-- 12. ORDER_ITEMS TABLE
-- ============================================
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    product_id BIGINT,
    quantity INTEGER NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- ============================================
-- 13. DELIVERIES TABLE
-- ============================================
CREATE TABLE deliveries (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    driver_id VARCHAR(128),
    distance_km DECIMAL(6,2),
    goods_amount DECIMAL(12,2),
    shipping_fee DECIMAL(12,2),
    driver_income DECIMAL(12,2),
    payment_method VARCHAR(50),
    store_name VARCHAR(255),
    shipping_address TEXT,
    customer_name VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE SET NULL
);

CREATE INDEX idx_deliveries_order_id ON deliveries(order_id);
CREATE INDEX idx_deliveries_driver_id ON deliveries(driver_id);
CREATE INDEX idx_deliveries_status ON deliveries(status);
CREATE INDEX idx_deliveries_started_at ON deliveries(started_at);

-- ============================================
-- 14. CART_ITEMS TABLE
-- ============================================
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(128) NOT NULL,
    store_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT uk_cart_items_customer_product UNIQUE (customer_id, product_id)
);

CREATE INDEX idx_cart_items_customer_id ON cart_items(customer_id);
CREATE INDEX idx_cart_items_store_id ON cart_items(store_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);

-- ============================================
-- 15. WISHLISTS TABLE
-- ============================================
CREATE TABLE wishlists (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(128) NOT NULL,
    store_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT uk_wishlists_customer_store UNIQUE (customer_id, store_id)
);

CREATE INDEX idx_wishlists_customer_id ON wishlists(customer_id);
CREATE INDEX idx_wishlists_store_id ON wishlists(store_id);

-- ============================================
-- 16. STORE_REVIEWS TABLE
-- ============================================
CREATE TABLE store_reviews (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    store_id BIGINT,
    customer_id VARCHAR(128),
    rating INTEGER NOT NULL,
    comment TEXT,
    image_url TEXT,
    reply TEXT,
    replied_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE
);

CREATE INDEX idx_store_reviews_store_id ON store_reviews(store_id);
CREATE INDEX idx_store_reviews_customer_id ON store_reviews(customer_id);
CREATE INDEX idx_store_reviews_order_id ON store_reviews(order_id);

-- ============================================
-- 17. VOUCHERS TABLE
-- ============================================
CREATE TABLE vouchers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    title TEXT,
    description TEXT,
    discount_type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(10,2),
    min_order_value DECIMAL(12,2),
    max_discount DECIMAL(12,2),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    usage_limit INTEGER,
    usage_count INTEGER DEFAULT 0,
    store_id BIGINT,
    status VARCHAR(50) NOT NULL,
    is_active BOOLEAN,
    is_created_by_admin BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE SET NULL
);

CREATE INDEX idx_vouchers_code ON vouchers(code);
CREATE INDEX idx_vouchers_store_id ON vouchers(store_id);
CREATE INDEX idx_vouchers_status ON vouchers(status);

-- ============================================
-- 18. WALLETS TABLE
-- ============================================
CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) UNIQUE NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    total_deposited DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_withdrawn DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_earned DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE
);

CREATE INDEX idx_wallet_user_id ON wallets(user_id);

-- ============================================
-- 19. WALLET_TRANSACTIONS TABLE
-- ============================================
CREATE TABLE wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(15,2) NOT NULL,
    balance_before DECIMAL(15,2),
    balance_after DECIMAL(15,2),
    description VARCHAR(500),
    reference_id VARCHAR(255),
    reference_type VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE
);

CREATE INDEX idx_wallet_trans_wallet ON wallet_transactions(wallet_id);
CREATE INDEX idx_wallet_trans_type ON wallet_transactions(transaction_type);
CREATE INDEX idx_wallet_trans_date ON wallet_transactions(created_at);
CREATE INDEX idx_wallet_trans_status ON wallet_transactions(status);
CREATE INDEX idx_wallet_trans_reference ON wallet_transactions(reference_id, reference_type);

-- ============================================
-- 20. WALLET_TOPUP_TRANSACTIONS TABLE
-- ============================================
CREATE TABLE wallet_topup_transactions (
    id BIGSERIAL PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    provider_transaction_id VARCHAR(100) UNIQUE,
    payment_url VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE
);

CREATE INDEX idx_topup_wallet ON wallet_topup_transactions(wallet_id);
CREATE INDEX idx_topup_provider_tx ON wallet_topup_transactions(provider_transaction_id);
CREATE INDEX idx_topup_status ON wallet_topup_transactions(status);
CREATE INDEX idx_topup_created_at ON wallet_topup_transactions(created_at);

-- ============================================
-- 21. CONVERSATIONS TABLE
-- ============================================
CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(128) NOT NULL,
    seller_id VARCHAR(128) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE,
    CONSTRAINT uk_conversations_customer_seller UNIQUE (customer_id, seller_id)
);

CREATE INDEX idx_conversations_customer_id ON conversations(customer_id);
CREATE INDEX idx_conversations_seller_id ON conversations(seller_id);

-- ============================================
-- 22. MESSAGES TABLE
-- ============================================
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id VARCHAR(128) NOT NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE
);

CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);

-- ============================================
-- 23. NOTIFICATIONS TABLE
-- ============================================
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT false,
    entity_id BIGINT,
    entity_type VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_type ON notifications(type);

-- ============================================
-- 24. DEVICE_TOKENS TABLE
-- ============================================
CREATE TABLE device_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    role VARCHAR(50) NOT NULL,
    token TEXT NOT NULL,
    platform VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE,
    CONSTRAINT uk_device_tokens_user_token UNIQUE (user_id, token)
);

CREATE INDEX idx_device_tokens_user_id ON device_tokens(user_id);
CREATE INDEX idx_device_tokens_role ON device_tokens(role);

-- ============================================
-- 25. SEARCH_HISTORY TABLE
-- ============================================
CREATE TABLE search_history (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    keyword VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE
);

CREATE INDEX idx_search_history_user_id ON search_history(user_id);
CREATE INDEX idx_search_history_keyword ON search_history(keyword);
CREATE INDEX idx_search_history_created_at ON search_history(created_at);

-- ============================================
-- 26. BANNERS TABLE
-- ============================================
CREATE TABLE banners (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    image_url TEXT,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_banners_status ON banners(status);
CREATE INDEX idx_banners_start_date ON banners(start_date);
CREATE INDEX idx_banners_end_date ON banners(end_date);

-- ============================================
-- INITIAL DATA: Insert default roles
-- ============================================
INSERT INTO roles (name, description) VALUES
('ADMIN', 'System administrator with full access'),
('CUSTOMER', 'Regular customer who can place orders'),
('SELLER', 'Store owner who can manage products and orders'),
('DRIVER', 'Delivery driver who can accept and complete deliveries');

-- ============================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================
COMMENT ON TABLE users IS 'Main users table storing user authentication and profile data';
COMMENT ON TABLE stores IS 'Stores/restaurants in the system';
COMMENT ON TABLE products IS 'Products sold by stores';
COMMENT ON TABLE orders IS 'Customer orders';
COMMENT ON TABLE deliveries IS 'Delivery information for orders';
COMMENT ON TABLE vouchers IS 'Discount vouchers created by admin or stores';
COMMENT ON TABLE wallets IS 'User wallet balances';
COMMENT ON TABLE wallet_transactions IS 'All wallet transaction history';
COMMENT ON TABLE conversations IS 'Chat conversations between customers and sellers';
COMMENT ON TABLE messages IS 'Chat messages';
COMMENT ON TABLE notifications IS 'User notifications';

COMMENT ON COLUMN stores.view_count IS 'Number of views for this store';
COMMENT ON COLUMN vouchers.usage_count IS 'Number of times this voucher has been used';
COMMENT ON COLUMN drivers.status IS 'Driver online/offline status (ONLINE, OFFLINE, BUSY, etc.)';
COMMENT ON COLUMN wallet_transactions.status IS 'Transaction status (PENDING, COMPLETED, FAILED, etc.)';
