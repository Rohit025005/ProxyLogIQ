"use client";

import { useEffect, useState } from "react";
import { getAnalyticsSummary, AnalyticsSummary } from "@/lib/api";
import { Activity, Database, Clock, AlertTriangle } from "lucide-react";

export default function KpiCards() {
  const [data, setData] = useState<AnalyticsSummary | null>(null);

  useEffect(() => {
    getAnalyticsSummary().then(setData).catch(console.error);
  }, []);

  if (!data) return <div className="grid grid-cols-4 gap-4"><p className="col-span-4 text-zinc-500">Loading...</p></div>;

  const cards = [
    { label: "Total Requests", value: data.totalRequests.toLocaleString(), icon: Activity, color: "text-blue-600" },
    { label: "Cache Hit Ratio", value: `${data.cacheHitRatio.toFixed(1)}%`, icon: Database, color: "text-green-600" },
    { label: "Avg Latency", value: `${data.averageLatency.toFixed(0)}ms`, icon: Clock, color: "text-orange-600" },
    { label: "Invalid Logs", value: data.invalidLogCount.toLocaleString(), icon: AlertTriangle, color: "text-red-600" },
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card) => (
        <div key={card.label} className="bg-white rounded-xl border border-zinc-200 p-5 shadow-sm">
          <div className="flex items-center justify-between">
            <p className="text-sm font-medium text-zinc-500">{card.label}</p>
            <card.icon className={`w-5 h-5 ${card.color}`} />
          </div>
          <p className="text-2xl font-bold mt-2 text-zinc-900">{card.value}</p>
        </div>
      ))}
    </div>
  );
}
