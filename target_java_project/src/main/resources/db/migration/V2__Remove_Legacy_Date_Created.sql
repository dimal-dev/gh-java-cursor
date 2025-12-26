-- Remove legacy date_created column from billing_order table
-- The table already has created_at and updated_at from BaseEntity

ALTER TABLE billing_order DROP COLUMN IF EXISTS date_created;
