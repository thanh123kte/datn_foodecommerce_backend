-- Add is_deleted column to banners table for soft delete
ALTER TABLE banners ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT false;
