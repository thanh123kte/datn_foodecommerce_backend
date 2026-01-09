-- V3__update_store_reviews_and_add_review_images.sql
-- Migration to update store_reviews table and create review_images table

-- ============================================
-- 1. Drop old image_url column from store_reviews
-- ============================================
ALTER TABLE store_reviews DROP COLUMN IF EXISTS image_url;

-- ============================================
-- 2. Create review_images table
-- ============================================
CREATE TABLE IF NOT EXISTS review_images (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_image_store_review 
        FOREIGN KEY (review_id) 
        REFERENCES store_reviews(id) 
        ON DELETE CASCADE
);

-- ============================================
-- 3. Create indexes for review_images
-- ============================================
CREATE INDEX IF NOT EXISTS idx_review_images_review_id ON review_images(review_id);

-- ============================================
-- 4. Add comment for documentation
-- ============================================
COMMENT ON TABLE review_images IS 'Stores multiple images for store reviews';
COMMENT ON COLUMN review_images.review_id IS 'Reference to the store review';
COMMENT ON COLUMN review_images.image_url IS 'URL of the review image';
