# ProxyLogIQ — Enterprise Proxy Log Analytics Platform

A full-stack enterprise-grade proxy log analytics platform built with **Java 17 / Spring Boot** backend and **Next.js 16** frontend. Upload proxy log files, parse them line-by-line, store valid and invalid entries in PostgreSQL, compute analytics, and visualize insights on a dashboard.

---

## Table of Contents

- [What It Does](#what-it-does)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
  - [Option 1: Docker Compose (Recommended)](#option-1-docker-compose-recommended)
  - [Option 2: Manual Setup](#option-2-manual-setup)
- [API Endpoints](#api-endpoints)
- [Dashboard Features](#dashboard-features)
- [Testing](#testing)
- [Project Walkthrough](#project-walkthrough)
- [Resume Value](#resume-value)

---

## What It Does

ProxyLogIQ ingests proxy server log files in pipe-delimited format and provides:

1. **Log Ingestion** — Upload `.log` or `.txt` files via REST API. Files are processed line-by-line using `BufferedReader` (no loading the entire file into memory).
2. **Parsing & Validation** — Each line is parsed and validated. Fields: `timestamp | method | path | status_code | cache_status | latency_ms | bytes`. Malformed lines are stored separately with the exact error reason.
3. **Persistence** — Valid entries stored in `log_entries` table, invalid entries in `invalid_log_entries` table via JPA/Hibernate.
4. **Analytics Engine** — Computes: total requests, cache hit ratio (%), status code distribution, top requested paths, slowest requests, average latency, p50/p95/p99 latency percentiles, total bytes transferred, invalid log count, and error reason distribution.
5. **Filtering & Pagination** — Filter logs by HTTP method, status code, cache hit/miss, path (contains), and date range. Results are paginated and sortable.
6. **Dashboard** — Next.js dashboard with KPI cards, pie chart (status codes), bar chart (cache hit/miss), top endpoints, paginated valid/invalid logs tables, and multi-filter controls.
7. **API Documentation** — Swagger/OpenAPI UI at `/swagger-ui.html`.

### Sample Log Format

```
2024-01-15T10:23:45.123Z | GET | /api/users | 200 | HIT | 12 | 1024
2024-01-15T10:23:46.456Z | POST | /api/orders | 201 | MISS | 340 | 2048
```

Lines with wrong field count, bad timestamps, negative values, or invalid status codes are captured as invalid with a descriptive error reason.

---

## Tech Stack

### Backend
| Technology | Purpose |
|-----------|---------|
| Java 17 | Language |
| Spring Boot 3.2 | Framework |
| Spring Web | REST APIs |
| Spring Data JPA / Hibernate | ORM / persistence |
| PostgreSQL | Database |
| Springdoc OpenAPI | Swagger documentation |
| Maven | Build tool |
| JUnit 5 + Mockito | Unit testing |

### Frontend
| Technology | Purpose |
|-----------|---------|
| Next.js 16 (App Router) | React framework |
| TypeScript | Type safety |
| Tailwind CSS 4 | Styling |
| Recharts | Charts (pie, bar) |
| Lucide React | Icons |

### Infrastructure
| Technology | Purpose |
|-----------|---------|
| Docker | Containerization |
| Docker Compose | Multi-service orchestration |

---

---

## Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** — [Download](https://nodejs.org/)
- **PostgreSQL 15+** — [Download](https://www.postgresql.org/download/)
- **Docker & Docker Compose** (optional, for containerized setup) — [Download](https://docs.docker.com/get-docker/)

---

## Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Start all services (backend + PostgreSQL + frontend)
cd ProxyLogIQ
docker-compose up --build
```

**Access the application:**
| Service | URL |
|---------|-----|
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Frontend Dashboard | http://localhost:3000 |

### Option 2: Manual Setup

#### Step 1: Start PostgreSQL

Create a database named `proxlogiq`:

```bash
# Using psql
psql -U postgres
CREATE DATABASE proxlogiq;
\q

# Or via pgAdmin — create a database called "proxlogiq"
```

Default credentials (configured in `application.properties`):
- URL: `jdbc:postgresql://localhost:5432/proxlogiq`
- Username: `postgres`
- Password: `postgres`

#### Step 2: Run the Backend

```bash
cd ProxyLogIQ

# On Windows
mvn spring-boot:run

# On macOS/Linux
./mvnw spring-boot:run
```

The backend starts at **http://localhost:8080**. Swagger UI available at **http://localhost:8080/swagger-ui.html**.

#### Step 3: Run the Frontend

```bash
cd proxy-log-dashboard

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

The frontend starts at **http://localhost:3000**.

---

## API Endpoints

### Log Ingestion

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/logs/upload` | Upload a `.log` or `.txt` file. Parses line-by-line, stores valid and invalid entries. Returns counts. |

**Example:**
```bash
curl -X POST http://localhost:8080/api/logs/upload \
  -F "file=@data/sample.log"
```

**Response:**
```json
{
  "validCount": 20,
  "invalidCount": 4,
  "message": "File processed successfully"
}
```

### Analytics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/logs/analytics/summary` | Total requests, cache hit ratio, average latency, total bytes transferred, invalid count, p50/p95/p99 latency |
| GET | `/api/logs/analytics/status-distribution` | Map of status code to count |
| GET | `/api/logs/analytics/top-paths?limit=10` | Most requested paths, sorted by count descending |
| GET | `/api/logs/analytics/slowest?limit=10` | Slowest requests, sorted by latency descending |
| GET | `/api/logs/analytics/invalid-reasons` | Map of error reason to count for invalid entries |

### Log Listings (with Pagination & Filtering)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/logs/filter?method=GET&statusCode=200&cacheHit=true&startTime=...&endTime=...&page=0&size=20` | Filtered, paginated valid log entries |
| GET | `/api/logs/invalid?page=0&size=10` | Paginated invalid log entries |

**Available filter parameters:**
| Parameter | Type | Example |
|-----------|------|---------|
| `method` | string | `GET`, `POST`, `PUT`, `DELETE`, `PATCH` |
| `statusCode` | integer | `200`, `404`, `500` |
| `path` | string | `/api/users` (partial match) |
| `cacheHit` | boolean | `true` or `false` |
| `startTime` | ISO 8601 | `2024-01-15T00:00:00Z` |
| `endTime` | ISO 8601 | `2024-01-16T00:00:00Z` |
| `page` | integer (default 0) | Page number |
| `size` | integer (default 20) | Page size |

---



## Testing

The backend has **17 unit tests** covering the parser and analytics service:

```bash
cd ProxyLogIQ
mvn test
```

**Test output:**
```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```




## License

MIT
