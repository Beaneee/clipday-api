package com.clipday.api.dailyrecord;

import jakarta.validation.constraints.Size;

public record DailyRecordUpdateRequest(

        @Size(max = 2000, message = "메모는 2000자를 넘을 수 없습니다.")
        String memo,

        @Size(max = 500, message = "이미지 경로가 너무 깁니다.")
        String imageUrl
) {}