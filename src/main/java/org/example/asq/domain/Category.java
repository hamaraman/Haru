package org.example.asq.domain;

public enum Category {
    FREE("자유", "💬"),
    QUESTION("질문", "❓"),
    INFO("정보", "💡"),
    REVIEW("후기", "⭐");

    private final String label;
    private final String icon;

    Category(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    /** 문자열 카테고리명으로 안전하게 라벨/아이콘 조회 (대시보드 DTO용) */
    public static String labelOf(String name) {
        try { return valueOf(name).label; } catch (Exception e) { return name; }
    }

    public static String iconOf(String name) {
        try { return valueOf(name).icon; } catch (Exception e) { return "•"; }
    }
}
