# ProxyLogIQ - Enterprise Proxy Log Analytics Platform

A full-stack enterprise-style proxy log analytics platform built with Java/Spring Boot backend and Next.js frontend.

Backend: 42 files, 17 passing tests  
Frontend: React/Next.js dashboard with Recharts visualizations  
Infrastructure: Docker Compose (backend + PostgreSQL + frontend)

## Features

- **Log Ingestion**: Upload .log/.txt files via REST API; line-by-line processing with BufferedReader
- **Robust Parser**: Pipe-delimited log parsing with field validation (timestamp, method, path, status_code, cache_status, latency_ms, bytes)
- **Enterprise Architecture**: Layered design (Controller, Service, Repository, DTO, Exception Handler, Config)
- **Analytics Engine**: Cache hit ratio, latency percentiles (p50/p95/p99), status code distribution, top paths, slowest endpoints, invalid error reasons
- **RESTful API**: Paginated/sorted endpoints with dynamic filtering (method, status, cache, date range)
- **Global Exception Handling**: @ControllerAdvice with consistent JSON error responses
- **Full-Stack Dashboard**: Next.js dashboard with KPI cards, Recharts pie/bar charts, sortable tables, and multi-filter controls
- **Containerized**: Docker Compose for backend + PostgreSQL + frontend
- **Testing**: 17 JUnit tests (unit tests for parser and analytics service)
- **API Documentation**: Swagger/OpenAPI at `/swagger-ui.html`

## Tech Stack

### Backend
- Java 17, Spring Boot 3.2, Spring Data JPA / Hibernate
- PostgreSQL, Springdoc OpenAPI (Swagger)
- Maven, JUnit 5 + Mockito

### Frontend
- Next.js 16 (App Router), TypeScript, Tailwind CSS 4
- Recharts (pie/bar charts), Lucide React (icons)

### DevOps
- Docker Compose (backend + PostgreSQL + frontend)

## Quick Start

### Option 1: Docker Compose
```bash
docker-compose up --build
# Backend API: http://localhost:8080
# Frontend:    http://localhost:3000
# Swagger UI:  http://localhost:8080/swagger-ui.html
```

### Option 2: Manual
```bash
# Backend
cd ProxyLogIQ
mvn spring-boot:run

# Frontend
cd proxy-log-dashboard
npm run dev
```

## Project Structure

```
ProxyLogIQ/
├── src/main/java/com/example/proxlogiq/
│   ├── config/WebConfig.java          # CORS configuration
│   ├── controller/LogController.java  # REST endpoints
│   ├── dto/LogUploadResponseDto.java  # Data transfer objects
│   ├── entity/LogEntry.java           # JPA entities
│   ├── entity/InvalidLogEntry.java
│   ├── exception/GlobalExceptionHandler.java  # @ControllerAdvice
│   ├── exception/ErrorResponse.java
│   ├── repository/LogEntryRepository.java
│   ├── service/LogParserService.java  # Pipe-delimited parser
│   ├── service/LogIngestionService.java  # Persistence layer
│   └── service/AnalyticsService.java  # Metrics computation
├── src/test/java/.../service/
│   ├── LogParserServiceTest.java      # 7 parser tests
│   └── AnalyticsServiceTest.java      # 10 analytics tests
├── src/main/resources/
│   ├── application.properties
│   └── data/sample.log               # Sample data with valid + invalid lines
├── proxy-log-dashboard/               # Next.js frontend
│   └── src/
│       ├── app/dashboard/page.tsx     # Main dashboard layout
│       ├── components/
│       │   ├── KpiCards.tsx           # 4 KPI metric cards
│       │   ├── StatusCodeChart.tsx     # Pie chart
│       │   ├── CacheHitChart.tsx      # Bar chart
│       │   ├── TopEndpoints.tsx       # Horizontal bar list
│       │   ├── RecentLogsTable.tsx    # Paginated valid logs
│       │   ├── InvalidLogsTable.tsx   # Paginated invalid logs
│       │   └── LogFilters.tsx         # Multi-filter controls
│       └── lib/api.ts                 # Typed API client
├── docker-compose.yml
├── Dockerfile                         # Backend Dockerfile
└── pom.xml
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/logs/upload` | Upload and parse a log file |
| GET | `/api/logs/analytics/summary` | Total requests, hit ratio, avg latency, percentiles |
| GET | `/api/logs/analytics/status-distribution` | Status code breakdown |
| GET | `/api/logs/analytics/top-paths?limit=10` | Most requested paths |
| GET | `/api/logs/analytics/slowest?limit=10` | Slowest requests |
| GET | `/api/logs/analytics/invalid-reasons` | Invalid log error reasons |
| GET | `/api/logs/filter?method=GET&page=0&size=20` | Filtered + paginated valid logs |
| GET | `/api/logs/invalid?page=0&size=10` | Paginated invalid logs |

## Testing

```bash
cd ProxyLogIQ
mvn test
# 17 tests, 0 failures
```

## Sample Log Format

```
timestamp | method | path | status_code | cache_status | latency_ms | bytes
2024-01-15T10:23:45.123Z | GET | /api/users | 200 | HIT | 12 | 1024
```

See `src/main/resources/data/sample.log` for a full sample file with valid and intentionally invalid lines.
