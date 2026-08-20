package com.clipday.api.dailyrecord;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_record",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_daily_record_tab_date",
                columnNames = {"tab_id", "date"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyRecord {

    /** 탭(테마) 미지정 요청에 사용하는 기본 탭 식별자. */
    public static final String DEFAULT_TAB_ID = "default";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 클라이언트가 관리하는 탭(테마) 식별자. 같은 탭 안에서 날짜는 유일하다. */
    @Column(name = "tab_id", nullable = false, length = 100)
    private String tabId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DailyRecord(String tabId, LocalDate date, String memo, String imageUrl) {
        this.tabId = normalizeTabId(tabId);
        this.date = date;
        this.memo = memo;
        this.imageUrl = imageUrl;
    }

    public void update(String memo, String imageUrl) {
        this.memo = memo;
        this.imageUrl = imageUrl;
    }

    /** null·공백 tabId는 기본 탭으로 취급한다. (구버전 클라이언트 호환) */
    public static String normalizeTabId(String tabId) {
        return (tabId == null || tabId.isBlank()) ? DEFAULT_TAB_ID : tabId.trim();
    }
}
