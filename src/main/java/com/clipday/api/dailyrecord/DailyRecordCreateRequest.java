package com.clipday.api.dailyrecord;

import java.time.LocalDate;

public record DailyRecordCreateRequest(
        LocalDate date,
        String memo,
        String imageUrl
) {}