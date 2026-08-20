package com.clipday.api.dailyrecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("DailyRecord API")
class DailyRecordApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired DailyRecordRepository repository;

    // Spring Boot 4는 Jackson 3를 쓰므로 com.fasterxml ObjectMapper 빈이 없다.
    // 테스트 본문 직렬화용으로만 쓰는 것이라 직접 만든다.
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final LocalDate DATE = LocalDate.of(2026, 8, 12);

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @Test
    @DisplayName("기록을 생성하면 201과 저장된 내용을 돌려준다")
    void create() throws Exception {
        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("tabId", "default", "date", DATE.toString(), "memo", "첫 기록"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tabId").value("default"))
                .andExpect(jsonPath("$.date").value("2026-08-12"))
                .andExpect(jsonPath("$.memo").value("첫 기록"));
    }

    @Test
    @DisplayName("tabId를 생략하면 기본 탭(default)으로 저장된다")
    void createWithoutTabId() throws Exception {
        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("date", DATE.toString(), "memo", "탭 없음"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tabId").value("default"));
    }

    @Test
    @DisplayName("탭이 다르면 같은 날짜에도 각각 기록할 수 있다")
    void sameDateAcrossDifferentTabs() throws Exception {
        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("tabId", "default", "date", DATE.toString(), "memo", "일상"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("tabId", "trip", "date", DATE.toString(), "memo", "여행"))))
                .andExpect(status().isCreated());

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("같은 탭에 같은 날짜를 또 만들면 409를 돌려준다")
    void duplicateInSameTab() throws Exception {
        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("tabId", "default", "date", DATE.toString()))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("tabId", "default", "date", DATE.toString()))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("목록 조회는 요청한 탭의 기록만 돌려준다")
    void listIsScopedToTab() throws Exception {
        repository.save(new DailyRecord("default", DATE, "일상", null));
        repository.save(new DailyRecord("trip", DATE, "여행", null));

        mockMvc.perform(get("/api/records").param("tabId", "trip"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].memo").value("여행"));
    }

    @Test
    @DisplayName("탭+날짜로 단건을 조회할 수 있다")
    void findByDate() throws Exception {
        repository.save(new DailyRecord("trip", DATE, "여행", null));

        mockMvc.perform(get("/api/records/by-date")
                        .param("tabId", "trip")
                        .param("date", DATE.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memo").value("여행"));
    }

    @Test
    @DisplayName("없는 기록을 조회하면 404를 돌려준다")
    void notFound() throws Exception {
        mockMvc.perform(get("/api/records/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("날짜가 없으면 400을 돌려준다")
    void dateIsRequired() throws Exception {
        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("tabId", "default", "memo", "날짜 없음"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("수정 응답의 updatedAt은 갱신된 값이어야 한다")
    void updateReturnsFreshUpdatedAt() throws Exception {
        DailyRecord saved = repository.saveAndFlush(new DailyRecord("default", DATE, "전", null));

        String body = mockMvc.perform(put("/api/records/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("memo", "후"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memo").value("후"))
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> parsed = objectMapper.readValue(body, Map.class);
        assertThat(parsed.get("updatedAt").toString())
                .isGreaterThanOrEqualTo(parsed.get("createdAt").toString());

        DailyRecord reloaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(parsed.get("updatedAt")).isEqualTo(reloaded.getUpdatedAt().toString());
    }

    @Test
    @DisplayName("기록을 삭제하면 204를 돌려주고 실제로 사라진다")
    void deleteOne() throws Exception {
        DailyRecord saved = repository.save(new DailyRecord("default", DATE, "삭제 대상", null));

        mockMvc.perform(delete("/api/records/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("탭 일괄 삭제는 해당 탭의 기록만 지운다")
    void deleteByTab() throws Exception {
        repository.save(new DailyRecord("default", DATE, "남을 것", null));
        repository.save(new DailyRecord("trip", DATE, "지울 것", null));
        repository.save(new DailyRecord("trip", DATE.plusDays(1), "지울 것 2", null));

        mockMvc.perform(delete("/api/records").param("tabId", "trip"))
                .andExpect(status().isNoContent());

        assertThat(repository.findAllByTabIdOrderByDateAsc("trip")).isEmpty();
        assertThat(repository.findAllByTabIdOrderByDateAsc("default")).hasSize(1);
    }
}
