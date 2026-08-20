package com.clipday.api.dailyrecord;

import com.clipday.api.exception.DuplicateException;
import com.clipday.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyRecordService {

    private final DailyRecordRepository repository;

    // READ - 탭 단위 전체
    public List<DailyRecordResponse> findAllByTab(String tabId) {
        return repository.findAllByTabIdOrderByDateAsc(DailyRecord.normalizeTabId(tabId)).stream()
                .map(DailyRecordResponse::from)
                .toList();
    }

    // READ - 단건
    public DailyRecordResponse findById(Long id) {
        return DailyRecordResponse.from(getOrThrow(id));
    }

    // READ - 탭 + 날짜
    public DailyRecordResponse findByTabAndDate(String tabId, LocalDate date) {
        String normalizedTabId = DailyRecord.normalizeTabId(tabId);
        DailyRecord record = repository.findByTabIdAndDate(normalizedTabId, date)
                .orElseThrow(() -> new NotFoundException(
                        "기록을 찾을 수 없습니다. tabId=" + normalizedTabId + ", date=" + date));
        return DailyRecordResponse.from(record);
    }

    // CREATE
    @Transactional
    public DailyRecordResponse create(DailyRecordCreateRequest request) {
        String tabId = DailyRecord.normalizeTabId(request.tabId());
        if (repository.existsByTabIdAndDate(tabId, request.date())) {
            throw new DuplicateException(
                    "이미 해당 날짜에 기록이 있습니다. tabId=" + tabId + ", date=" + request.date());
        }
        DailyRecord saved = repository.save(
                new DailyRecord(tabId, request.date(), request.memo(), request.imageUrl())
        );
        return DailyRecordResponse.from(saved);
    }

    // UPDATE
    @Transactional
    public DailyRecordResponse update(Long id, DailyRecordUpdateRequest request) {
        DailyRecord record = getOrThrow(id);
        record.update(request.memo(), request.imageUrl());
        // @UpdateTimestamp는 flush 시점에 채워진다. flush 없이 응답을 만들면
        // updatedAt이 갱신 전 값으로 나가므로 먼저 반영한다.
        repository.flush();
        return DailyRecordResponse.from(record);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("기록을 찾을 수 없습니다. id=" + id);
        }
        repository.deleteById(id);
    }

    // DELETE - 탭 통째로 (탭 삭제 시)
    @Transactional
    public void deleteByTab(String tabId) {
        repository.deleteAllByTabId(DailyRecord.normalizeTabId(tabId));
    }

    private DailyRecord getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("기록을 찾을 수 없습니다. id=" + id));
    }
}
