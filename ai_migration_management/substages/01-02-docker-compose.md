# Sub-Stage 1.2: Docker Compose Setup

## Goal
Create Docker Compose configuration for local development infrastructure (PostgreSQL, Redis, Kafka).

---

## Files to Create

### 1. docker/docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:18.1
    container_name: goodhelp-postgres
    environment:
      POSTGRES_DB: goodhelp
      POSTGRES_USER: goodhelp
      POSTGRES_PASSWORD: goodhelp_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./postgres/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U goodhelp"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: goodhelp-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: goodhelp-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: goodhelp-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    healthcheck:
      test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
  redis_data:

networks:
  default:
    name: goodhelp-network
```

---

### 2. docker/postgres/init.sql

```sql
-- Initial database setup script
-- This runs when the PostgreSQL container is first created

-- Ensure the database exists (it's created by POSTGRES_DB env var, but just in case)
-- CREATE DATABASE IF NOT EXISTS goodhelp;

-- Set timezone
SET timezone = 'UTC';

-- Create extension for UUID generation if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE goodhelp TO goodhelp;

-- Create schemas if needed (using default public schema)
-- CREATE SCHEMA IF NOT EXISTS app;

-- Log that initialization is complete
DO $$
BEGIN
    RAISE NOTICE 'Database initialization complete';
END $$;
```

---

### 3. docker/.env.example

```env
# PostgreSQL
POSTGRES_DB=goodhelp
POSTGRES_USER=goodhelp
POSTGRES_PASSWORD=goodhelp_password

# Redis
REDIS_PASSWORD=

# Kafka
KAFKA_BROKER_ID=1
```

---

## Directory Structure

```
target_java_project/
└── docker/
    ├── docker-compose.yml
    ├── .env.example
    └── postgres/
        └── init.sql
```

---

## Usage Commands

### Start all services
```bash
cd target_java_project/docker
docker-compose up -d
```

### Stop all services
```bash
docker-compose down
```

### View logs
```bash
docker-compose logs -f
```

### View specific service logs
```bash
docker-compose logs -f postgres
docker-compose logs -f kafka
```

### Reset database (removes all data)
```bash
docker-compose down -v
docker-compose up -d
```

---

## Connection Details

| Service | Host | Port | Credentials |
|---------|------|------|-------------|
| PostgreSQL | localhost | 5432 | goodhelp / goodhelp_password |
| Redis | localhost | 6379 | (no auth) |
| Kafka | localhost | 9092 | (no auth) |
| Zookeeper | localhost | 2181 | (no auth) |

---

## JDBC Connection String
```
jdbc:postgresql://localhost:5432/goodhelp
```

---

## Verification

1. **Start services:**
   ```bash
   cd target_java_project/docker
   docker-compose up -d
   ```

2. **Check all containers are running:**
   ```bash
   docker-compose ps
   ```

3. **Test PostgreSQL connection:**
   ```bash
   docker exec -it goodhelp-postgres psql -U goodhelp -d goodhelp -c "SELECT 1"
   ```

4. **Test Redis connection:**
   ```bash
   docker exec -it goodhelp-redis redis-cli ping
   ```

5. **Verify Kafka is ready:**
   ```bash
   docker exec -it goodhelp-kafka kafka-topics --bootstrap-server localhost:9092 --list
   ```

---

## Checklist

- [ ] docker-compose.yml created
- [ ] postgres/init.sql created
- [ ] All containers start successfully
- [ ] PostgreSQL is accessible
- [ ] Redis is accessible
- [ ] Kafka is accessible

---

## Next Sub-Stage
Proceed to **1.3: Spring Boot Application Entry Point**

