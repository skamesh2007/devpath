package com.devpath.backend.repository;

import com.devpath.backend.entity.Roadmap;
import com.devpath.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    List<Roadmap> findByUser(User user);
}