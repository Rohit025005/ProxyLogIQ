"use client";

import { useEffect, useState } from "react";
import { getAnalyticsSummary, AnalyticsSummary } from "@/lib/api";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from "recharts";

export default function CacheHitChart() {
  const [data, setData] = useState<{ name: string; value: number }[]>([]);

  useEffect(() => {
    getAnalyticsSummary()
      .then((s: AnalyticsSummary) => {
        if (s.totalRequests === 0) { setData([]); return; }
        const total = s.totalRequests;
        const hits = Math.round((s.cacheHitRatio / 100) * total);
        const misses = total - hits;
        setData([
          { name: "HIT", value: hits },
          { name: "MISS", value: misses },
        ]);
      })
      .catch(console.error);
  }, []);

  if (data.length === 0) return <div className="h-64 flex items-center justify-center text-zinc-400">No data</div>;

  return (
    <div className="h-64" style={{ minHeight: "16rem" }}>
      <ResponsiveContainer width="100%" height={256}>
        <BarChart data={data}>
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="value" fill="#3b82f6" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
