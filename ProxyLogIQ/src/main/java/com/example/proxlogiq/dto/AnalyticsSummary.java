package com.example.proxlogiq.dto;

public class AnalyticsSummary {

    private long totalRequests;
    private double cacheHitRatio;
    private double averageLatency;
    private long totalBytesTransferred;
    private long invalidLogCount;
    private long p50;
    private long p95;
    private long p99;

    public AnalyticsSummary() {}

    public AnalyticsSummary(long totalRequests, double cacheHitRatio, double averageLatency,
                            long totalBytesTransferred, long invalidLogCount,
                            long p50, long p95, long p99) {
        this.totalRequests = totalRequests;
        this.cacheHitRatio = cacheHitRatio;
        this.averageLatency = averageLatency;
        this.totalBytesTransferred = totalBytesTransferred;
        this.invalidLogCount = invalidLogCount;
        this.p50 = p50;
        this.p95 = p95;
        this.p99 = p99;
    }

    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
    public double getCacheHitRatio() { return cacheHitRatio; }
    public void setCacheHitRatio(double cacheHitRatio) { this.cacheHitRatio = cacheHitRatio; }
    public double getAverageLatency() { return averageLatency; }
    public void setAverageLatency(double averageLatency) { this.averageLatency = averageLatency; }
    public long getTotalBytesTransferred() { return totalBytesTransferred; }
    public void setTotalBytesTransferred(long totalBytesTransferred) { this.totalBytesTransferred = totalBytesTransferred; }
    public long getInvalidLogCount() { return invalidLogCount; }
    public void setInvalidLogCount(long invalidLogCount) { this.invalidLogCount = invalidLogCount; }
    public long getP50() { return p50; }
    public void setP50(long p50) { this.p50 = p50; }
    public long getP95() { return p95; }
    public void setP95(long p95) { this.p95 = p95; }
    public long getP99() { return p99; }
    public void setP99(long p99) { this.p99 = p99; }
}
