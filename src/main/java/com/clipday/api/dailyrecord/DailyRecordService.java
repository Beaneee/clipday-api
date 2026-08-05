package com.clipday.api.dailyrecord;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyRecordService {

    private final DailyRecordRepository repository;

    public List<DailyRecordResponse> findAll() {
        return repository.findAll().stream()
                .map(DailyRecordResponse::from)
                .toList();
    }
}