package com.devpath.backend.repository;

import com.devpath.backend.entity.AIInsights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AIInsightsRepository extends JpaRepository<AIInsights, Long> {

    Optional<AIInsights> findByUserId(Long userId);


}
