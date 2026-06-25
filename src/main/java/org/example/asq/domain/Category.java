package org.example.asq.domain;

public enum Category {
    FREE("자유"),
    QUESTION("질문"),
    INFO("정보"),
    REVIEW("후기");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
