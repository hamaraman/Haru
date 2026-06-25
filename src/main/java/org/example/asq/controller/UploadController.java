package org.example.asq.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
public class UploadController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    /** 동영상 허용 확장자 (브라우저가 content-type을 application/octet-stream으로 보내는 경우 대비) */
    private static final Set<String> VIDEO_EXT =
            Set.of("mp4", "mov", "webm", "mkv", "avi", "m4v", "ogv", "3gp");

    private static final long MAX_VIDEO_BYTES = 1024L * 1024 * 1024; // 1GB

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

        if (file.getSize() > 10L * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미지는 10MB 이하여야 합니다."));
        }

        return save(file);
    }

    @PostMapping("/upload/video")
    public ResponseEntity<Map<String, Object>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        if (!isVideo(file)) {
            return ResponseEntity.badRequest().body(Map.of("error", "동영상 파일만 업로드 가능합니다."));
        }

        if (file.getSize() > MAX_VIDEO_BYTES) {
            return ResponseEntity.status(413).body(Map.of(
                    "error", "동영상은 1GB 이하여야 합니다. (현재 "
                            + (file.getSize() / (1024 * 1024)) + "MB)"));
        }

        return save(file);
    }

    private boolean isVideo(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video/")) return true;
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        return ext != null && VIDEO_EXT.contains(ext.toLowerCase());
    }

    private ResponseEntity<Map<String, Object>> save(MultipartFile file) {
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

    /** 멀티파트 용량 초과 시 깔끔한 JSON 에러 반환 (프론트에서 메시지 표시) */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleTooLarge(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(413).body(Map.of(
                "error", "파일이 너무 큽니다. 동영상은 최대 1GB까지 업로드할 수 있어요."));
    }
}
