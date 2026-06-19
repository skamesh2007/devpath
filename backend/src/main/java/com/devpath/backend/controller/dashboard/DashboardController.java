package com.devpath.backend.controller.dashboard;

import com.devpath.backend.dto.roadmap.CareerMomentumResponse;
import com.devpath.backend.dto.dashboard.DashboardResponse;
import com.devpath.backend.service.roadmap.CareerMomentumService;
import com.devpath.backend.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CareerMomentumService careerMomentumService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(
                dashboardService.getDashboard()
        );
    }

    @GetMapping("/momentum")
    public ResponseEntity<CareerMomentumResponse> getCareerMomentum() {
        return ResponseEntity.ok(
                careerMomentumService.getCareerMomentum()
        );
    }
}