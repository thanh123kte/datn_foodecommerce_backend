-- Add is_deleted columns for soft delete filtering
ALTER TABLE addresses ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE products ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE store_categories ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE vouchers ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT false;