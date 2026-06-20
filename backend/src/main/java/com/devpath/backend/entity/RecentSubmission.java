package com.devpath.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "leetcode_recent_submissions")
public class RecentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String titleSlug;

    private String statusDisplay;

    private String lang;

    private String timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leetcode_stats_id")
    @JsonIgnore
    private LeetCodeStats leetCodeStats;
}