package com.clipday.api.dailyrecord;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class DailyRecordController {

    private final DailyRecordService service;

    // GET /api/records
    @GetMapping
    public List<DailyRecordResponse> getAll() {
        return service.findAll();
    }

    // GET /api/records/1
    @GetMapping("/{id}")
    public DailyRecordResponse getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    // POST /api/records
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DailyRecordResponse create(@Valid @RequestBody DailyRecordCreateRequest request) {
        return service.create(request);
    }

    // PUT /api/records/1
    @PutMapping("/{id}")
    public DailyRecordResponse update(
            @PathVariable Long id,
            @Valid @RequestBody DailyRecordUpdateRequest request
    ) {
        return service.update(id, request);
    }

    // DELETE /api/records/1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}