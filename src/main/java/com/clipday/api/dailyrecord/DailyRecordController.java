package com.clipday.api.dailyrecord;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "DailyRecord", description = "날짜별 기록 API")
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class DailyRecordController {

    private final DailyRecordService service;

    // GET /api/records?tabId=default
    @Operation(summary = "탭별 기록 조회", description = "tabId 생략 시 기본 탭(default)")
    @GetMapping
    public List<DailyRecordResponse> getAll(
            @RequestParam(required = false) String tabId
    ) {
        return service.findAllByTab(tabId);
    }

    // GET /api/records/by-date?tabId=default&date=2026-08-12
    @Operation(summary = "탭+날짜로 단건 조회")
    @GetMapping("/by-date")
    public DailyRecordResponse getByDate(
            @RequestParam(required = false) String tabId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.findByTabAndDate(tabId, date);
    }

    // GET /api/records/1
    @Operation(summary = "단건 기록 조회")
    @GetMapping("/{id}")
    public DailyRecordResponse getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    // POST /api/records
    @Operation(summary = "기록 생성", description = "탭 하나당 날짜당 하나만 생성 가능")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DailyRecordResponse create(@Valid @RequestBody DailyRecordCreateRequest request) {
        return service.create(request);
    }

    // PUT /api/records/1
    @Operation(summary = "기록 수정")
    @PutMapping("/{id}")
    public DailyRecordResponse update(
            @PathVariable Long id,
            @Valid @RequestBody DailyRecordUpdateRequest request
    ) {
        return service.update(id, request);
    }

    // DELETE /api/records/1
    @Operation(summary = "기록 삭제")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // DELETE /api/records?tabId=trip
    @Operation(summary = "탭 기록 일괄 삭제", description = "탭 삭제 시 해당 탭의 기록을 모두 제거")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByTab(@RequestParam String tabId) {
        service.deleteByTab(tabId);
    }
}
