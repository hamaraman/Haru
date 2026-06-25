package org.example.asq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberStatsDto {
    private long postCount;
    private long commentCount;
    private long totalLikes;
}
