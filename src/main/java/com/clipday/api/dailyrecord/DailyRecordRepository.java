package com.clipday.api.dailyrecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {

    List<DailyRecord> findAllByTabIdOrderByDateAsc(String tabId);

    Optional<DailyRecord> findByTabIdAndDate(String tabId, LocalDate date);

    boolean existsByTabIdAndDate(String tabId, LocalDate date);

    void deleteAllByTabId(String tabId);
}
