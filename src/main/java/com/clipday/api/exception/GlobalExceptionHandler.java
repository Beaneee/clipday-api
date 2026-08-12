package com.clipday.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - 입력값 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(), "Bad Request", message);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 - 잘못된 요청 (파일 검증 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    // 413 - 파일 용량 초과
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSize(MaxUploadSizeExceededException e) {
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.PAYLOAD_TOO_LARGE.value(), "Payload Too Large",
                "파일 크기가 너무 큽니다. (최대 10MB)");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }

    // 404 - 리소스 없음
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(), "Not Found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 409 - 중복
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateException e) {
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.CONFLICT.value(), "Conflict", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 500 - 그 외 예상 못한 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                "서버 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}