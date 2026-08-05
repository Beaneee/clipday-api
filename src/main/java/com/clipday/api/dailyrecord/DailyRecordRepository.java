package com.clipday.api.dailyrecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {

    Optional<DailyRecord> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);
}