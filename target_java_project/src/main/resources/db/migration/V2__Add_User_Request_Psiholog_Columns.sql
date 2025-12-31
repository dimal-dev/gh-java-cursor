-- Add missing columns to user_request_therapist table
ALTER TABLE user_request_therapist
    ADD COLUMN IF NOT EXISTS is_processed INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS lgbtq INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS channel VARCHAR(500),
    ADD COLUMN IF NOT EXISTS problem TEXT,
    ADD COLUMN IF NOT EXISTS consultation_type VARCHAR(500),
    ADD COLUMN IF NOT EXISTS sex VARCHAR(50) DEFAULT 'both',
    ADD COLUMN IF NOT EXISTS price VARCHAR(500),
    ADD COLUMN IF NOT EXISTS promocode VARCHAR(500),
    ADD COLUMN IF NOT EXISTS additional_data JSONB DEFAULT '{}'::jsonb;

-- Update existing records to have default values
UPDATE user_request_therapist
SET is_processed = 0,
    lgbtq = 0,
    sex = 'both',
    additional_data = '{}'::jsonb
WHERE is_processed IS NULL OR lgbtq IS NULL OR sex IS NULL OR additional_data IS NULL;

