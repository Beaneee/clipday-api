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
@Table(name = "daily_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
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

    public DailyRecord(LocalDate date, String memo, String imageUrl) {
        this.date = date;
        this.memo = memo;
        this.imageUrl = imageUrl;
    }

    public void update(String memo, String imageUrl) {
        this.memo = memo;
        this.imageUrl = imageUrl;
    }
}