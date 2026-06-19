package com.devpath.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_insights")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIInsights {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @Builder.Default
    private List<String> strengths = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    private List<String> weaknesses = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    private List<String> nextActions = new ArrayList<>();

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}
