package com.clipday.api.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService storageService;

    @PostMapping
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        String imageUrl = storageService.store(file);
        return Map.of("imageUrl", imageUrl);
    }
}