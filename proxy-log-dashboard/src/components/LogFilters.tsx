"use client";

import { useState } from "react";
import { LogFilters as FilterType } from "@/lib/api";

const METHODS = ["", "GET", "POST", "PUT", "DELETE", "PATCH"];
const STATUSES = ["", "200", "201", "204", "301", "400", "401", "403", "404", "500", "502", "503"];
const CACHE_OPTIONS = ["", "HIT", "MISS"];

interface Props {
  onApply: (filters: FilterType) => void;
}

export default function LogFilters({ onApply }: Props) {
  const [method, setMethod] = useState("");
  const [statusCode, setStatusCode] = useState("");
  const [cache, setCache] = useState("");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");

  const handleApply = () => {
    onApply({
      method: method || undefined,
      statusCode: statusCode ? Number(statusCode) : undefined,
      cacheHit: cache === "HIT" ? true : cache === "MISS" ? false : undefined,
      startTime: startTime ? new Date(startTime).toISOString() : undefined,
      endTime: endTime ? new Date(endTime).toISOString() : undefined,
    });
  };

  return (
    <div className="flex flex-wrap items-end gap-3 pb-4 border-b border-zinc-200">
      <div>
        <label className="block text-xs text-zinc-500 mb-1">Method</label>
        <select value={method} onChange={(e) => setMethod(e.target.value)}
          className="border border-zinc-300 rounded px-3 py-1.5 text-sm bg-white">
          {METHODS.map((m) => <option key={m} value={m}>{m || "All"}</option>)}
        </select>
      </div>
      <div>
        <label className="block text-xs text-zinc-500 mb-1">Status</label>
        <select value={statusCode} onChange={(e) => setStatusCode(e.target.value)}
          className="border border-zinc-300 rounded px-3 py-1.5 text-sm bg-white">
          {STATUSES.map((s) => <option key={s} value={s}>{s || "All"}</option>)}
        </select>
      </div>
      <div>
        <label className="block text-xs text-zinc-500 mb-1">Cache</label>
        <select value={cache} onChange={(e) => setCache(e.target.value)}
          className="border border-zinc-300 rounded px-3 py-1.5 text-sm bg-white">
          {CACHE_OPTIONS.map((c) => <option key={c} value={c}>{c || "All"}</option>)}
        </select>
      </div>
      <div>
        <label className="block text-xs text-zinc-500 mb-1">From</label>
        <input type="datetime-local" value={startTime} onChange={(e) => setStartTime(e.target.value)}
          className="border border-zinc-300 rounded px-3 py-1.5 text-sm" />
      </div>
      <div>
        <label className="block text-xs text-zinc-500 mb-1">To</label>
        <input type="datetime-local" value={endTime} onChange={(e) => setEndTime(e.target.value)}
          className="border border-zinc-300 rounded px-3 py-1.5 text-sm" />
      </div>
      <button onClick={handleApply}
        className="bg-blue-600 text-white px-4 py-1.5 rounded text-sm hover:bg-blue-700 transition-colors">
        Apply Filters
      </button>
    </div>
  );
}
