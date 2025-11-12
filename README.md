# Famme Product Manager

A Spring Boot application for managing products with automatic synchronization from the Famme.no API. Built with JdbcClient (no JPA/Hibernate), Flyway migrations, HTMX for dynamic UI updates, and PostgreSQL in Docker.

## Prerequisites

### Required

#### 1. Docker Desktop

- [Download for Mac](https://www.docker.com/products/docker-desktop)
- [Download for Windows](https://www.docker.com/products/docker-desktop)
- [Download for Linux](https://docs.docker.com/desktop/install/linux-install/)

#### 2. Java 24

Install via [SDKMAN](https://sdkman.io/) (recommended):
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 24.0.2-oracle
sdk use java 24.0.2-oracle
```

Or download directly from [Oracle](https://www.oracle.com/java/technologies/downloads/)

### Optional

- **IntelliJ IDEA** (for HTTP Client testing and Database Tool)

## Getting Started

### Step 1: Start PostgreSQL
```bash
docker-compose up -d
```

**What this does:**
- Downloads PostgreSQL 18 Docker image (first time only - ~80MB)
- Starts PostgreSQL container in the background
- Creates `famme_products` database automatically
- Exposes PostgreSQL on port 5432

### Step 2: Run the Application
```bash
./gradlew bootRun
```

### Step 3: Open Your Browser
```
http://localhost:8080
```
