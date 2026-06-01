package com.example.proxlogiq.repository;

import com.example.proxlogiq.entity.InvalidLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvalidLogEntryRepository extends JpaRepository<InvalidLogEntry, Long> {

    // Custom query methods
    List<InvalidLogEntry> findByCreatedAtAfter(LocalDateTime dateTime);
    List<InvalidLogEntry> findByErrorReasonContaining(String errorReason);
}