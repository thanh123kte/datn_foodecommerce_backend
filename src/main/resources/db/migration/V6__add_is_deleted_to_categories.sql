-- Add is_deleted column to categories table for soft delete
ALTER TABLE categories ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT false;
