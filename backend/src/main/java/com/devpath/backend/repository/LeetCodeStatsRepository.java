package com.devpath.backend.repository;

import com.devpath.backend.entity.LeetCodeStats;
import com.devpath.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeetCodeStatsRepository
        extends JpaRepository<LeetCodeStats, Long> {
    Optional<LeetCodeStats> findByUser(User user);

    Optional<LeetCodeStats> findByUsername(String username);

}