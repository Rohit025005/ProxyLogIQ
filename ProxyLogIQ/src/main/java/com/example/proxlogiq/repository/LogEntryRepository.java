package com.example.proxlogiq.repository;

import com.example.proxlogiq.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByTimestampBetween(Instant start, Instant end);
    List<LogEntry> findByMethod(String method);
    List<LogEntry> findByStatusCode(Integer statusCode);
    List<LogEntry> findByCacheHit(Boolean cacheHit);
    Long countByCacheHit(Boolean cacheHit);

    List<LogEntry> findByOrderByLatencyMsDesc(Pageable pageable);
}