"use client";

import { useEffect, useState } from "react";
import { getFilteredLogs, LogEntry, LogFilters as FilterType } from "@/lib/api";

const PAGE_SIZE = 15;

interface Props {
  filters?: FilterType;
}

export default function RecentLogsTable({ filters = {} }: Props) {
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getFilteredLogs(filters, page, PAGE_SIZE)
      .then((res) => {
        setLogs(res.content);
        setTotalPages(res.totalPages);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [page, filters]);

  return (
    <div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-zinc-200 text-left text-zinc-500">
              <th className="pb-2 font-medium">Timestamp</th>
              <th className="pb-2 font-medium">Method</th>
              <th className="pb-2 font-medium">Path</th>
              <th className="pb-2 font-medium">Status</th>
              <th className="pb-2 font-medium">Cache</th>
              <th className="pb-2 font-medium">Latency</th>
              <th className="pb-2 font-medium">Bytes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={7} className="pt-4 text-zinc-400">Loading...</td></tr>
            ) : logs.length === 0 ? (
              <tr><td colSpan={7} className="pt-4 text-zinc-400">No logs found</td></tr>
            ) : (
              logs.map((log) => (
                <tr key={log.id} className="border-b border-zinc-100 hover:bg-zinc-50">
                  <td className="py-2 text-zinc-600">{new Date(log.timestamp).toLocaleString()}</td>
                  <td className="py-2">
                    <span className={`font-medium ${log.method === "GET" ? "text-green-600" : "text-blue-600"}`}>
                      {log.method}
                    </span>
                  </td>
                  <td className="py-2 max-w-[200px] truncate text-zinc-700">{log.path}</td>
                  <td className="py-2">
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                      log.statusCode < 300 ? "bg-green-100 text-green-700" :
                      log.statusCode < 400 ? "bg-blue-100 text-blue-700" :
                      log.statusCode < 500 ? "bg-orange-100 text-orange-700" :
                      "bg-red-100 text-red-700"
                    }`}>
                      {log.statusCode}
                    </span>
                  </td>
                  <td className="py-2">
                    <span className={log.cacheHit ? "text-green-600" : "text-zinc-400"}>
                      {log.cacheHit ? "HIT" : "MISS"}
                    </span>
                  </td>
                  <td className="py-2 text-zinc-600">{log.latencyMs}ms</td>
                  <td className="py-2 text-zinc-600">{(log.bytes / 1024).toFixed(1)}KB</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
      <div className="flex items-center justify-between mt-4">
        <p className="text-sm text-zinc-500">Page {page + 1} of {totalPages}</p>
        <div className="flex gap-2">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            className="px-3 py-1 text-sm border rounded disabled:opacity-40 hover:bg-zinc-50"
          >
            Previous
          </button>
          <button
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            className="px-3 py-1 text-sm border rounded disabled:opacity-40 hover:bg-zinc-50"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}
