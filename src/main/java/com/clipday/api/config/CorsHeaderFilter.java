package com.clipday.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CORS 헤더를 필터 체인 가장 앞에서 붙인다.
 *
 * Spring MVC의 CORS 처리는 요청이 핸들러까지 도달해야 동작한다. 업로드 용량
 * 초과(413)처럼 그 전에 끊기는 응답에는 헤더가 붙지 않아서, 브라우저가 응답을
 * 차단하고 네트워크 오류로 처리한다. 서버는 413을 제대로 보냈는데도 클라이언트
 * 쪽에서는 "연결 실패"로 보이는 문제가 생긴다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getHeader("Origin") != null) {
            response.setHeader("Access-Control-Allow-Origin", "*"); // 배포 시 변경
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        filterChain.doFilter(request, response);
    }
}
