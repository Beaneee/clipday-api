package com.clipday.api.dailyrecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyRecordResponse(
        Long id,
        LocalDate date,
        String memo,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DailyRecordResponse from(DailyRecord entity) {
        return new DailyRecordResponse(
                entity.getId(),
                entity.getDate(),
                entity.getMemo(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}