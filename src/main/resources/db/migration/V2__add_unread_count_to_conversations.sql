-- Add unread count columns for customer and seller
ALTER TABLE conversations 
ADD COLUMN unread_count_customer INTEGER DEFAULT 0 NOT NULL,
ADD COLUMN unread_count_seller INTEGER DEFAULT 0 NOT NULL;

-- Update existing conversations to have 0 unread count
UPDATE conversations 
SET unread_count_customer = 0, 
    unread_count_seller = 0
WHERE unread_count_customer IS NULL 
   OR unread_count_seller IS NULL;

-- Add indexes for better performance on unread count queries
CREATE INDEX idx_conversations_unread_customer ON conversations(customer_id, unread_count_customer) WHERE unread_count_customer > 0;
CREATE INDEX idx_conversations_unread_seller ON conversations(seller_id, unread_count_seller) WHERE unread_count_seller > 0;
