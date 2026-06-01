"use client";

import { useEffect, useState } from "react";
import { getStatusCodeDistribution } from "@/lib/api";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";

const COLORS = ["#22c55e", "#3b82f6", "#f59e0b", "#ef4444", "#8b5cf6"];

export default function StatusCodeChart() {
  const [data, setData] = useState<{ name: string; value: number }[]>([]);

  useEffect(() => {
    getStatusCodeDistribution()
      .then((raw) => {
        const chartData = Object.entries(raw)
          .map(([code, count]) => ({ name: code, value: count }))
          .sort((a, b) => b.value - a.value);
        setData(chartData);
      })
      .catch(console.error);
  }, []);

  if (data.length === 0) return <div className="h-64 flex items-center justify-center text-zinc-400">No data</div>;

  return (
    <div className="h-64" style={{ minHeight: "16rem" }}>
      <ResponsiveContainer width="100%" height={256}>
        <PieChart>
          <Pie data={data} dataKey="value" nameKey="name" outerRadius={80} label={({ name }) => name}>
            {data.map((_, i) => (
              <Cell key={i} fill={COLORS[i % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}
