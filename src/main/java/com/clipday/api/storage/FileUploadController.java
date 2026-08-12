package com.clipday.api.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Image", description = "이미지 업로드 API")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService storageService;

    @Operation(summary = "이미지 업로드", description = "최대 10MB, 이미지 파일만 가능")
    @PostMapping
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        String imageUrl = storageService.store(file);
        return Map.of("imageUrl", imageUrl);
    }
}