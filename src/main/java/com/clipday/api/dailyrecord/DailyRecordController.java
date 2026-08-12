package com.clipday.api.dailyrecord;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "DailyRecord", description = "날짜별 기록 API")
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class DailyRecordController {

    private final DailyRecordService service;

    // GET /api/records
    @Operation(summary = "전체 기록 조회")
    @GetMapping
    public List<DailyRecordResponse> getAll() {
        return service.findAll();
    }

    // GET /api/records/1
    @Operation(summary = "단건 기록 조회")
    @GetMapping("/{id}")
    public DailyRecordResponse getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    // POST /api/records
    @Operation(summary = "기록 생성", description = "날짜당 하나만 생성 가능")
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
}