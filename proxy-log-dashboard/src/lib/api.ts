export type LogEntry = {
  id: number;
  timestamp: string;
  method: string;
  path: string;
  statusCode: number;
  cacheHit: boolean;
  latencyMs: number;
  bytes: number;
};

export type InvalidLogEntry = {
  id: number;
  rawLine: string;
  errorReason: string;
  createdAt: string;
};

export type AnalyticsSummary = {
  totalRequests: number;
  cacheHitRatio: number;
  averageLatency: number;
  totalBytesTransferred: number;
  invalidLogCount: number;
  p50: number;
  p95: number;
  p99: number;
};

export type LogFilters = {
  method?: string;
  statusCode?: number;
  path?: string;
  cacheHit?: boolean;
  startTime?: string;
  endTime?: string;
};

const API_BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/logs";

async function fetchJson<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, {
    headers: { "Content-Type": "application/json", ...init?.headers },
    ...init,
  });
  if (!res.ok) {
    const body = await res.text();
    throw new Error(`API ${res.status}: ${body}`);
  }
  return res.json();
}

export function getAnalyticsSummary(): Promise<AnalyticsSummary> {
  return fetchJson(`${API_BASE}/analytics/summary`);
}

export function getStatusCodeDistribution(): Promise<Record<string, number>> {
  return fetchJson(`${API_BASE}/analytics/status-distribution`);
}

export type TopPath = { key: string; value: number };

export function getTopPaths(limit = 10): Promise<TopPath[]> {
  return fetchJson<Record<string, number>[]>(`${API_BASE}/analytics/top-paths?limit=${limit}`)
    .then((data) => data.map((item) => {
      const key = Object.keys(item)[0];
      return { key, value: item[key] };
    }));
}

export function getInvalidReasons(): Promise<Record<string, number>> {
  return fetchJson(`${API_BASE}/analytics/invalid-reasons`);
}

export function getFilteredLogs(
  filters: LogFilters,
  page = 0,
  size = 20
): Promise<{ content: LogEntry[]; totalElements: number; totalPages: number }> {
  const params = new URLSearchParams();
  if (filters.method) params.set("method", filters.method);
  if (filters.statusCode !== undefined) params.set("statusCode", String(filters.statusCode));
  if (filters.path) params.set("path", filters.path);
  if (filters.cacheHit !== undefined) params.set("cacheHit", String(filters.cacheHit));
  if (filters.startTime) params.set("startTime", filters.startTime);
  if (filters.endTime) params.set("endTime", filters.endTime);
  params.set("page", String(page));
  params.set("size", String(size));
  return fetchJson(`${API_BASE}/filter?${params}`);
}

export function getInvalidLogs(
  page = 0,
  size = 20
): Promise<{ content: InvalidLogEntry[]; totalElements: number; totalPages: number }> {
  return fetchJson(`${API_BASE}/invalid?page=${page}&size=${size}`);
}

export function uploadLogFile(file: File): Promise<{ validCount: number; invalidCount: number; message: string }> {
  const formData = new FormData();
  formData.append("file", file);
  return fetch(`${API_BASE}/upload`, { method: "POST", body: formData }).then((r) => {
    if (!r.ok) {
      return r.text().then((body) => { throw new Error(`API ${r.status}: ${body}`); });
    }
    return r.json();
  });
}
