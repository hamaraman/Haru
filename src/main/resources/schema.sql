-- 하루 커뮤니티 게시판 DDL

CREATE TABLE IF NOT EXISTS member (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password      VARCHAR(255),
    name          VARCHAR(30),
    phone         VARCHAR(20),
    nickname      VARCHAR(30)  NOT NULL,
    profile_image VARCHAR(255),
    provider      VARCHAR(20)  NOT NULL DEFAULT 'local',
    social_id     VARCHAR(100),
    noti_comment  TINYINT(1)   NOT NULL DEFAULT 1,
    noti_like     TINYINT(1)   NOT NULL DEFAULT 1,
    noti_notice   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS post (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    category   VARCHAR(20)  NOT NULL DEFAULT 'FREE',
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    member_id  BIGINT       NOT NULL,
    view_count INT          NOT NULL DEFAULT 0,
    like_count INT          NOT NULL DEFAULT 0,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS comment (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT   NOT NULL,
    member_id  BIGINT   NOT NULL,
    content    TEXT     NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id)   REFERENCES post   (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS post_like (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id   BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    UNIQUE KEY uq_post_member (post_id, member_id),
    FOREIGN KEY (post_id)   REFERENCES post   (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id)
);

-- 샘플 데이터 (비밀번호: 1234 → BCrypt 해시)
INSERT INTO member (email, password, nickname, provider) VALUES
('admin@haru.kr', '$2a$10$qsd5D8V9RnHtTNUhgzPHyO3qf8miVEGKPBfs8WvXW4pzW8WuEp9QK', '운영자', 'local'),
('user1@haru.kr', '$2a$10$qsd5D8V9RnHtTNUhgzPHyO3qf8miVEGKPBfs8WvXW4pzW8WuEp9QK', '봄날', 'local'),
('user2@haru.kr', '$2a$10$qsd5D8V9RnHtTNUhgzPHyO3qf8miVEGKPBfs8WvXW4pzW8WuEp9QK', '여름이', 'local'),
('tester@haru.kr', '$2a$10$qsd5D8V9RnHtTNUhgzPHyO3qf8miVEGKPBfs8WvXW4pzW8WuEp9QK', '테스터', 'local');

INSERT INTO post (category, title, content, member_id, view_count, like_count) VALUES
('FREE',     '하루에 오신 것을 환영합니다',    '매일 들르는 이야기, 하루입니다. 편하게 글을 남겨보세요!', 1, 120, 15),
('QUESTION', 'Spring Boot 3 와 4 차이가 뭔가요?','Spring Boot 4가 새로 나왔는데 주요 변경사항이 궁금합니다.', 2, 87,  8),
('INFO',     'Pretendard 폰트 설치 방법',       '웹 폰트로 Pretendard를 사용하는 방법을 공유합니다.', 3, 54,  5),
('REVIEW',   '카페에서 코딩 후기',              '오늘 조용한 카페에서 하루 종일 코딩했는데 생산성이 엄청 올랐어요!', 2, 43, 3);

INSERT INTO comment (post_id, member_id, content) VALUES
(1, 2, '반갑습니다! 좋은 커뮤니티가 될 것 같아요.'),
(1, 3, '열심히 활동하겠습니다 :)'),
(2, 1, 'Spring Boot 4는 Spring Framework 6.2 기반입니다.');
