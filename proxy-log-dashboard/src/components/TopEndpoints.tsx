"use client";

import { useEffect, useState } from "react";
import { getTopPaths, TopPath } from "@/lib/api";

export default function () {
  const [paths, setPaths] = useState<TopPath[]>([]);

  useEffect(() => {
    getTopPaths(10)
      .then((data) => setPaths(data))
      .catch(console.error);
  }, []);

  if (paths.length === 0) return <p className="text-zinc-400 text-sm">No data</p>;

  const maxCount = paths[0]?.value || 1;

  return (
    <div className="space-y-2">
      {paths.map((item, i) => (
        <div key={item.key} className="flex items-center gap-3">
          <span className="text-xs text-zinc-400 w-4">{i + 1}</span>
          <span className="text-sm text-zinc-700 truncate flex-1">{item.key}</span>
          <div className="flex items-center gap-2">
            <div className="h-2 bg-blue-500 rounded-full" style={{ width: `${(item.value / maxCount) * 80}px` }} />
            <span className="text-xs text-zinc-500 w-10 text-right">{item.value}</span>
          </div>
        </div>
      ))}
    </div>
  );
}
