package org.example.asq.controller;

import jakarta.servlet.http.HttpSession;
import org.example.asq.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
public class UploadController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미지 파일만 업로드 가능합니다."));
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일 크기는 10MB 이하여야 합니다."));
        }

        try {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
            Path dest = Paths.get(uploadDir, filename);
            Files.createDirectories(dest.getParent());
            file.transferTo(dest.toFile());
            return ResponseEntity.ok(Map.of("url", "/uploads/" + filename));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "업로드 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/upload/video")
    public ResponseEntity<Map<String, Object>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "동영상 파일만 업로드 가능합니다."));
        }

        if (file.getSize() > 200 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일 크기는 200MB 이하여야 합니다."));
        }

        try {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
            Path dest = Paths.get(uploadDir, filename);
            Files.createDirectories(dest.getParent());
            file.transferTo(dest.toFile());
            return ResponseEntity.ok(Map.of("url", "/uploads/" + filename));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "업로드 실패: " + e.getMessage()));
        }
    }
}
