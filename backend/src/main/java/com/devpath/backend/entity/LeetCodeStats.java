package com.devpath.backend.entity;

import com.devpath.backend.dto.leetcode.LeetCodeStatsResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder()
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leetcode_stats")
public class LeetCodeStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String username;

    private Integer ranking;

    private Integer totalSolved;

    private Integer easySolved;

    private Integer mediumSolved;

    private Integer hardSolved;

    private LocalDateTime lastUpdated;

    @OneToMany(
            mappedBy = "leetCodeStats",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<RecentSubmission> recentSubmissions = new ArrayList<>();

}