"use client";

import { useState } from "react";
import KpiCards from "@/components/KpiCards";
import StatusCodeChart from "@/components/StatusCodeChart";
import CacheHitChart from "@/components/CacheHitChart";
import TopEndpoints from "@/components/TopEndpoints";
import RecentLogsTable from "@/components/RecentLogsTable";
import InvalidLogsTable from "@/components/InvalidLogsTable";
import LogFilters from "@/components/LogFilters";
import { LogFilters as FilterType } from "@/lib/api";

export default function DashboardPage() {
  const [filters, setFilters] = useState<FilterType>({});

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-zinc-900">Dashboard</h1>
        <p className="text-zinc-500 text-sm mt-1">Proxy log analytics overview</p>
      </div>

      <KpiCards />

      <LogFilters onApply={setFilters} />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl border border-zinc-200 p-5 shadow-sm">
          <h2 className="text-sm font-semibold text-zinc-700 mb-3">Status Code Distribution</h2>
          <StatusCodeChart />
        </div>
        <div className="bg-white rounded-xl border border-zinc-200 p-5 shadow-sm">
          <h2 className="text-sm font-semibold text-zinc-700 mb-3">Cache Hit Ratio</h2>
          <CacheHitChart />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl border border-zinc-200 p-5 shadow-sm">
          <h2 className="text-sm font-semibold text-zinc-700 mb-3">Top 10 Endpoints</h2>
          <TopEndpoints />
        </div>
        <div className="bg-white rounded-xl border border-zinc-200 p-5 shadow-sm">
          <h2 className="text-sm font-semibold text-zinc-700 mb-3">Invalid Log Error Reasons</h2>
          <p className="text-zinc-400 text-sm italic">Available after data ingestion</p>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-zinc-200 p-5 shadow-sm">
        <h2 className="text-sm font-semibold text-zinc-700 mb-3">Recent Valid Logs</h2>
        <RecentLogsTable filters={filters} />
      </div>

      <div className="bg-white rounded-xl border border-zinc-200 p-5 shadow-sm">
        <h2 className="text-sm font-semibold text-zinc-700 mb-3">Invalid Logs</h2>
        <InvalidLogsTable />
      </div>
    </div>
  );
}
