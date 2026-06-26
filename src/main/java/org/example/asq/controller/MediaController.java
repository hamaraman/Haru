package org.example.asq.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 업로드된 미디어 파일(/uploads/**)을 서빙한다.
 * - 이미지: 기존처럼 누구나 열람 가능 (캐시 허용)
 * - 영상: 로그인 + 같은 사이트(Referer) 에서의 임베드 재생만 허용하고,
 *         HTTP Range 스트리밍으로만 내려줘 통째 다운로드를 어렵게 한다.
 * 이 컨트롤러가 정적 리소스 핸들러(/uploads/**)를 대체한다. (WebConfig 참고)
 */
@RestController
public class MediaController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    /** 추가로 허용할 사이트 호스트 (리버스 프록시 등으로 Host가 다를 때 대비, 선택값) */
    @Value("${app.site-host:}")
    private String siteHost;

    private static final Set<String> VIDEO_EXT =
            Set.of("mp4", "mov", "webm", "mkv", "avi", "m4v", "ogv", "3gp");

    /** Range 한 청크 최대 크기 (1MB). 한 번에 전체 파일을 못 받게 쪼갠다. */
    private static final long CHUNK_SIZE = 1024L * 1024;

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<?> serve(@PathVariable String filename,
                                   @RequestHeader HttpHeaders headers,
                                   HttpServletRequest request,
                                   HttpSession session) throws IOException {

        // 1) 경로 탈출(path traversal) 방지: uploadDir 밖으로 못 나가게 한다.
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path path = base.resolve(filename).normalize();
        if (!path.startsWith(base) || !Files.isRegularFile(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(path);
        String ext = StringUtils.getFilenameExtension(filename);
        boolean isVideo = ext != null && VIDEO_EXT.contains(ext.toLowerCase());
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        // ── 이미지 등: 기존대로 공개 서빙 ──
        if (!isVideo) {
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                    .contentLength(resource.contentLength())
                    .body(resource);
        }

        // ── 영상: 서버측 보호 ──
        // 2) 로그인 사용자만
        if (session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // 3) 우리 사이트에서 임베드된 재생만 허용 (핫링크 / 주소창 직접 접근 / 외부 다운로더 차단)
        if (!isAllowedReferer(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        long contentLength = resource.contentLength();
        List<HttpRange> ranges = headers.getRange();

        long start;
        long end;
        if (ranges.isEmpty()) {
            // Range 없이 통째로 요청해도 첫 청크만 내려준다 → 전체 다운로드 방지
            start = 0;
            end = Math.min(CHUNK_SIZE, contentLength) - 1;
        } else {
            HttpRange range = ranges.get(0);
            start = range.getRangeStart(contentLength);
            end = range.getRangeEnd(contentLength);
            end = Math.min(end, start + CHUNK_SIZE - 1);
        }
        long rangeLength = end - start + 1;

        ResourceRegion region = new ResourceRegion(resource, start, rangeLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header("X-Content-Type-Options", "nosniff")
                .body(region);
    }

    /**
     * 요청이 우리 사이트의 페이지에서 비롯됐는지 확인한다.
     * Referer(없으면 Origin)의 호스트가 우리 서버 호스트와 같아야 한다.
     * 헤더가 아예 없으면(주소창 직접 입력/외부 다운로더) 차단한다.
     * 리버스 프록시 환경을 고려해 여러 호스트 후보와 비교한다.
     */
    private boolean isAllowedReferer(HttpServletRequest request) {
        String ref = request.getHeader(HttpHeaders.REFERER);
        if (ref == null) ref = request.getHeader(HttpHeaders.ORIGIN);
        if (ref == null) return false;
        String refHost;
        try {
            refHost = URI.create(ref).getHost();
        } catch (IllegalArgumentException e) {
            return false;
        }
        if (refHost == null) return false;

        // 우리 사이트로 인정할 호스트 후보들 (프록시가 Host를 바꿔도 매칭되도록)
        if (refHost.equalsIgnoreCase(request.getServerName())) return true;
        if (!siteHost.isBlank() && refHost.equalsIgnoreCase(siteHost)) return true;
        if (matchesHostHeader(refHost, request.getHeader("X-Forwarded-Host"))) return true;
        if (matchesHostHeader(refHost, request.getHeader(HttpHeaders.HOST))) return true;
        return false;
    }

    /** "host" 또는 "host:port" 형태의 헤더 값에서 호스트만 떼어 비교한다. */
    private boolean matchesHostHeader(String refHost, String headerValue) {
        if (headerValue == null || headerValue.isBlank()) return false;
        // X-Forwarded-Host 는 "a.com, b.com" 처럼 여러 값일 수 있어 첫 값을 쓴다.
        String first = headerValue.split(",")[0].trim();
        int colon = first.indexOf(':');
        String host = colon >= 0 ? first.substring(0, colon) : first;
        return refHost.equalsIgnoreCase(host);
    }
}
