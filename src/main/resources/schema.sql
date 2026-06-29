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
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
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

-- 이 파일은 스키마(DDL) 전용입니다. 샘플/시드 데이터는 두지 않습니다.
-- 로컬 개발용 샘플 데이터가 필요하면 data-sample.sql 을 직접 실행하세요.
-- 관리자 계정은 절대 고정 비밀번호로 커밋하지 마세요. (data-sample.sql 상단 안내 참고)
