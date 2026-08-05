package com.clipday.api.dailyrecord;

public record DailyRecordUpdateRequest(
        String memo,
        String imageUrl
) {}