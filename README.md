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

## Dashboard Features

The Next.js dashboard (http://localhost:3000/dashboard) includes:

1. **KPI Cards** — Total Requests, Cache Hit Ratio (%), Average Latency (ms), Invalid Log Count
2. **Status Code Distribution** — Pie chart showing proportion of each HTTP status code
3. **Cache Hit/Miss** — Bar chart showing HIT vs MISS counts
4. **Top 10 Endpoints** — Horizontal bar list of most requested paths
5. **Recent Valid Logs** — Paginated table with timestamp, method, path, status, cache, latency, bytes
6. **Invalid Logs** — Paginated table with raw line and error reason
7. **Filter Controls** — Dropdowns for Method, Status Code, Cache Status, and Date Range (From/To)

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

### Test Breakdown

**LogParserServiceTest (7 tests):**
- Parse a valid line and verify all fields
- Reject line with wrong field count
- Reject line with invalid timestamp
- Reject line with negative latency
- Reject line with invalid status code
- Parse a multi-line file and correctly separate valid/invalid entries
- Skip empty and comment lines

**AnalyticsServiceTest (10 tests):**
- Total request count
- Cache hit ratio calculation (50% for 1 hit out of 2)
- Cache hit ratio returns 0 for empty dataset
- Average latency calculation
- Status code distribution grouping
- Top paths returns sorted results
- Slowest requests via repository call
- Filter by method
- Filter returns empty for no match
- Latency percentiles return correct keys

---

## Project Walkthrough (for interviews)

### Architecture Overview

```
┌──────────────────────────────────────────────────────────────┐
│                     Frontend (Next.js 16)                    │
│  ┌─────────┐ ┌──────────┐ ┌────────────┐ ┌──────────────┐    │
│  │KPI Cards│ │  Charts  │ │  Tables    │ │   Filters    │    │
│  └────┬────┘ └────┬─────┘ └─────┬──────┘ └──────┬───────┘    │
│       └───────────┴─────────────┴───────────────┘            │
│                         │ REST API                           │
└─────────────────────────┼────────────────────────────────────┘
                          │
┌─────────────────────────┼───────────────────────────────────┐
│               Backend (Spring Boot)                         │
│  ┌────────────┐  ┌──────────────┐  ┌────────────────────┐   │
│  │ Controller │──│   Service    │──│   Repository       │   │
│  │ (REST API) │  │ (Business)   │  │ (Data Access)      │   │
│  └────────────┘  └──────┬───────┘  └─────────┬──────────┘   │
│                         │                    │              │
│                  ┌──────┴──────┐             │              │
│                  │LogParser    │             │              │
│                  │Service      │             │              │
│                  └─────────────┘             │              │
│                                        ┌─────┴─────┐        │
│                                        │ PostgreSQL │       │
│                                        └───────────┘        │
└─────────────────────────────────────────────────────────────┘
```

### Key Design Decisions

1. **Layered Architecture** — Controller → Service → Repository keeps concerns separated, making the code testable and maintainable.

2. **Line-by-line Processing** — Using `BufferedReader` instead of reading the entire file into memory ensures the system can handle large log files without `OutOfMemoryError`.

3. **Separate Valid/Invalid Storage** — Invalid lines are stored with their error reason, allowing admins to inspect what went wrong and fix the source data. The `InvalidLogEntry` entity captures the raw line and the parsing error.

4. **Pagination & Filtering** — All listing endpoints accept `Pageable` and filter parameters. The `Page` response includes metadata (`totalElements`, `totalPages`) needed for frontend pagination.

5. **Global Exception Handling** — A single `@ControllerAdvice` catches all exceptions and returns consistent JSON error responses, avoiding `500` error pages and leaking stack traces.

6. **DTO Pattern** — The upload endpoint returns a `LogUploadResponseDto` instead of exposing entity internals. This decouples the API contract from the database schema.

### What I Would Improve with More Time

- **JPA Specifications** — Replace in-memory filtering with database-level `Specification` queries for better performance on large datasets
- **Caching** — Cache frequently accessed analytics results in Redis
- **Authentication** — Spring Security with JWT tokens to protect upload endpoints
- **Rate Limiting** — Prevent abuse of the upload endpoint
- **Audit Logging** — Track who uploaded which file and when

---

## Resume Value

This project demonstrates the following skills sought after in Java/Spring Boot fresher roles:

| Skill | Demonstrated By |
|-------|----------------|
| **Java** | 42 source files, streams, lambdas, optionals |
| **Spring Boot** | Auto-configuration, dependency injection, REST controllers |
| **Spring Data JPA** | Entity mapping, repository pattern, custom queries |
| **PostgreSQL** | Database schema, JPA/Hibernate integration |
| **REST API Design** | 8 well-structured endpoints with pagination/filtering |
| **Exception Handling** | `@ControllerAdvice`, consistent error responses |
| **Testing** | 17 JUnit/Mockito tests covering core logic |
| **Docker** | Docker Compose for 3 services, multi-stage build |
| **Frontend Integration** | Next.js consuming REST APIs with typed client |
| **Project Structure** | Layered architecture, DTOs, separation of concerns |
| **Code Quality** | No comments, clean naming, single responsibility |

---

## License

MIT
