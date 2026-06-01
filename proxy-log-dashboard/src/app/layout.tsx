import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Link from "next/link";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "ProxyLogIQ Dashboard",
  description: "Enterprise Proxy Log Analytics Platform",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}>
      <body className="min-h-full flex flex-col bg-zinc-50">
        <header className="border-b border-zinc-200 bg-white">
          <div className="max-w-7xl mx-auto px-6 h-14 flex items-center justify-between">
            <Link href="/" className="text-lg font-bold text-zinc-900">
              ProxyLogIQ
            </Link>
            <nav className="flex gap-6 text-sm text-zinc-600">
              <Link href="/dashboard" className="hover:text-zinc-900 transition-colors">Dashboard</Link>
              <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noopener noreferrer"
                className="hover:text-zinc-900 transition-colors">API Docs</a>
            </nav>
          </div>
        </header>
        <main className="flex-1 max-w-7xl mx-auto w-full">
          {children}
        </main>
      </body>
    </html>
  );
}
