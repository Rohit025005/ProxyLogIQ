"use client";

import { useEffect, useState } from "react";
import { getInvalidLogs, InvalidLogEntry } from "@/lib/api";

const PAGE_SIZE = 10;

export default function InvalidLogsTable() {
  const [logs, setLogs] = useState<InvalidLogEntry[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getInvalidLogs(page, PAGE_SIZE)
      .then((res) => {
        setLogs(res.content);
        setTotalPages(res.totalPages);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [page]);

  return (
    <div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-zinc-200 text-left text-zinc-500">
              <th className="pb-2 font-medium">#</th>
              <th className="pb-2 font-medium">Raw Line</th>
              <th className="pb-2 font-medium">Error</th>
              <th className="pb-2 font-medium">Created</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={4} className="pt-4 text-zinc-400">Loading...</td></tr>
            ) : logs.length === 0 ? (
              <tr><td colSpan={4} className="pt-4 text-zinc-400">No invalid logs</td></tr>
            ) : (
              logs.map((log) => (
                <tr key={log.id} className="border-b border-zinc-100 hover:bg-zinc-50">
                  <td className="py-2 text-zinc-500">{log.id}</td>
                  <td className="py-2 max-w-[300px] truncate font-mono text-xs text-zinc-700">{log.rawLine}</td>
                  <td className="py-2">
                    <span className="px-2 py-0.5 rounded text-xs bg-red-100 text-red-700">{log.errorReason}</span>
                  </td>
                  <td className="py-2 text-zinc-600">{new Date(log.createdAt).toLocaleString()}</td>
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
