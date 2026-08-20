package com.clipday.api.dailyrecord;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DailyRecordCreateRequest(

        @Size(max = 100, message = "탭 식별자가 너무 깁니다.")
        String tabId,

        @NotNull(message = "날짜는 필수입니다.")
        LocalDate date,

        @Size(max = 2000, message = "메모는 2000자를 넘을 수 없습니다.")
        String memo,

        @Size(max = 500, message = "이미지 경로가 너무 깁니다.")
        String imageUrl
) {}
