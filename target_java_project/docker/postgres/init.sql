-- Initial database setup script
-- This runs when the PostgreSQL container is first created

-- Set timezone
SET timezone = 'UTC';

-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE goodhelp TO goodhelp;

-- Log that initialization is complete
DO $$
BEGIN
    RAISE NOTICE 'GoodHelp database initialization complete';
END $$;
