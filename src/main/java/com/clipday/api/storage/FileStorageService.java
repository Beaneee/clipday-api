package com.clipday.api.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉토리를 생성할 수 없습니다.", e);
        }
    }

    public String store(MultipartFile file) {
        validate(file);

        String extension = extractExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + extension;
        Path target = uploadPath.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }

        return "/images/" + storedName;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
    }

    private String extractExtension(String originalName) {
        if (originalName == null) return "";
        int dotIndex = originalName.lastIndexOf('.');
        return dotIndex == -1 ? "" : originalName.substring(dotIndex);
    }
}