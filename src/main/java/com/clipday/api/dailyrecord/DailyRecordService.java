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

    // READ - 전체
    public List<DailyRecordResponse> findAll() {
        return repository.findAll().stream()
                .map(DailyRecordResponse::from)
                .toList();
    }

    // READ - 단건
    public DailyRecordResponse findById(Long id) {
        DailyRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기록을 찾을 수 없습니다. id=" + id));
        return DailyRecordResponse.from(record);
    }

    // CREATE
    @Transactional
    public DailyRecordResponse create(DailyRecordCreateRequest request) {
        if (repository.existsByDate(request.date())) {
            throw new IllegalStateException("이미 해당 날짜에 기록이 있습니다. date=" + request.date());
        }
        DailyRecord saved = repository.save(
                new DailyRecord(request.date(), request.memo(), request.imageUrl())
        );
        return DailyRecordResponse.from(saved);
    }

    // UPDATE
    @Transactional
    public DailyRecordResponse update(Long id, DailyRecordUpdateRequest request) {
        DailyRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기록을 찾을 수 없습니다. id=" + id));
        record.update(request.memo(), request.imageUrl());
        return DailyRecordResponse.from(record);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("기록을 찾을 수 없습니다. id=" + id);
        }
        repository.deleteById(id);
    }
}