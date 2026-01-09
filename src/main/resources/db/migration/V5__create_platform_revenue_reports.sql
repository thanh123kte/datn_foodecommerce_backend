-- Create platform_revenue_reports table for admin reporting
CREATE TABLE IF NOT EXISTS platform_revenue_reports (
    id SERIAL PRIMARY KEY,
    store_id INTEGER NOT NULL,
    seller_id VARCHAR(255) NOT NULL,
    total_orders INTEGER NOT NULL DEFAULT 0,
    total_revenue DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    total_discount DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    total_shipping_fee DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    total_commission DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    report_type VARCHAR(20) NOT NULL DEFAULT 'DAILY',
    report_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_platform_revenue_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_platform_revenue_seller FOREIGN KEY (seller_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_platform_revenue_store_id ON platform_revenue_reports(store_id);
CREATE INDEX IF NOT EXISTS idx_platform_revenue_seller_id ON platform_revenue_reports(seller_id);
CREATE INDEX IF NOT EXISTS idx_platform_revenue_report_date ON platform_revenue_reports(report_date);
CREATE INDEX IF NOT EXISTS idx_platform_revenue_report_type ON platform_revenue_reports(report_type);
CREATE INDEX IF NOT EXISTS idx_platform_revenue_date_type ON platform_revenue_reports(report_date, report_type);
