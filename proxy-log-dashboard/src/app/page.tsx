import Link from "next/link";

export default function Home() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-3.5rem)] px-6">
      <h1 className="text-4xl font-bold text-zinc-900 mb-4">ProxyLogIQ</h1>
      <p className="text-lg text-zinc-500 mb-8 text-center max-w-md">
        Enterprise Proxy Log Analytics Platform
      </p>
      <Link
        href="/dashboard"
        className="bg-blue-600 text-white px-6 py-3 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
      >
        Go to Dashboard
      </Link>
    </div>
  );
}
