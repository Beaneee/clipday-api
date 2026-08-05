package com.clipday.api.dailyrecord;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class DailyRecordController {

    private final DailyRecordService service;

    @GetMapping
    public List<DailyRecordResponse> getAll() {
        return service.findAll();
    }
}