-- ============================================================
-- 로컬 개발 전용 샘플 데이터 — 운영 DB에서는 절대 실행하지 마세요.
-- (Spring 자동 실행 대상이 아니도록 파일명을 data.sql 이 아닌 data-sample.sql 로 둡니다.)
--
-- 아래 테스트 계정 비밀번호는 모두 "1234" 입니다. 로컬 테스트 용도로만 쓰세요.
-- ============================================================

-- 테스트 일반 사용자 (id: 1=봄날, 2=여름이, 3=테스터)
INSERT INTO member (email, password, nickname, provider) VALUES
('user1@haru.kr',  '$2a$10$qsd5D8V9RnHtTNUhgzPHyO3qf8miVEGKPBfs8WvXW4pzW8WuEp9QK', '봄날',   'local'),
('user2@haru.kr',  '$2a$10$qsd5D8V9RnHtTNUhgzPHyO3qf8miVEGKPBfs8WvXW4pzW8WuEp9QK', '여름이', 'local'),
('tester@haru.kr', '$2a$10$qsd5D8V9RnHtTNUhgzPHyO3qf8miVEGKPBfs8WvXW4pzW8WuEp9QK', '테스터', 'local');

INSERT INTO post (category, title, content, member_id, view_count, like_count) VALUES
('FREE',     '하루에 오신 것을 환영합니다',    '매일 들르는 이야기, 하루입니다. 편하게 글을 남겨보세요!', 1, 120, 15),
('QUESTION', 'Spring Boot 3 와 4 차이가 뭔가요?','Spring Boot 4가 새로 나왔는데 주요 변경사항이 궁금합니다.', 2, 87,  8),
('INFO',     'Pretendard 폰트 설치 방법',       '웹 폰트로 Pretendard를 사용하는 방법을 공유합니다.', 3, 54,  5),
('REVIEW',   '카페에서 코딩 후기',              '오늘 조용한 카페에서 하루 종일 코딩했는데 생산성이 엄청 올랐어요!', 1, 43, 3);

INSERT INTO comment (post_id, member_id, content) VALUES
(1, 2, '반갑습니다! 좋은 커뮤니티가 될 것 같아요.'),
(1, 3, '열심히 활동하겠습니다 :)'),
(2, 1, 'Spring Boot 4는 Spring Framework 6.2 기반입니다.');

-- ============================================================
-- 관리자 계정 만들기 (admin@haru.kr 이메일이 곧 관리자 권한임 — AdminController.isAdmin 참고)
--
-- 1) 강한 비밀번호의 BCrypt 해시를 직접 생성하세요. 예:
--      - 온라인 BCrypt 생성기 대신, 앱의 PasswordEncoder 로 생성하는 것을 권장
--      - 또는: new BCryptPasswordEncoder().encode("강한비밀번호")
-- 2) 아래 주석을 풀고 <YOUR_BCRYPT_HASH> 자리에 넣어 한 번만 실행하세요.
--    절대 이 파일에 실제 해시를 커밋하지 마세요.
--
-- INSERT INTO member (email, password, nickname, provider)
-- VALUES ('admin@haru.kr', '<YOUR_BCRYPT_HASH>', '운영자', 'local');
-- ============================================================
