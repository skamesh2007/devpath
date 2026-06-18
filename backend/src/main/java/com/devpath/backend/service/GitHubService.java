package com.devpath.backend.service;

import com.devpath.backend.dto.*;
import com.devpath.backend.entity.User;
import com.devpath.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RestClient githubRestClient;

    public GitHubStatsResponse getStats() {

        User user = authService.getCurrentUser();

        String githubUsername = user.getGithubUsername();

        if (githubUsername == null || githubUsername.isBlank()) {
            throw new IllegalStateException(
                    "GitHub username not configured"
            );
        }

        Map<String, Object> profile =
                githubRestClient.get()
                        .uri("/users/{username}", githubUsername)
                        .retrieve()
                        .body(Map.class);

        List<Map<String, Object>> repos =
                githubRestClient.get()
                        .uri("/users/{username}/repos?per_page=100",
                                githubUsername)
                        .retrieve()
                        .body(List.class);

        if (repos == null) {
            repos = List.of();
        }

        int totalStars = repos.stream()
                .mapToInt(repo ->
                        ((Number) repo.get("stargazers_count"))
                                .intValue()
                )
                .sum();

        return GitHubStatsResponse.builder()
                .username(githubUsername)
                .profileUrl((String) profile.get("html_url"))
                .avatarUrl((String) profile.get("avatar_url"))
                .publicRepos(
                        ((Number) profile.get("public_repos")).intValue()
                )
                .followers(
                        ((Number) profile.get("followers")).intValue()
                )
                .following(
                        ((Number) profile.get("following")).intValue()
                )
                .totalStars(totalStars)
                .build();
    }

    public GitHubUsernameResponse getUsername() {

        User user = authService.getCurrentUser();

        return GitHubUsernameResponse.builder()
                .githubUsername(user.getGithubUsername())
                .build();
    }

    public GitHubUsernameResponse saveUsername(
            GitHubUsernameRequest request
    ) {

        User user = authService.getCurrentUser();

        // Allow removing username
        if (request.getGithubUsername() == null ||
                request.getGithubUsername().isBlank()) {

            user.setGithubUsername(null);

            userRepository.save(user);

            return GitHubUsernameResponse.builder()
                    .githubUsername(null)
                    .build();
        }

        String githubUsername =
                request.getGithubUsername().trim();

        try {

            Map<String, Object> profile =
                    githubRestClient.get()
                            .uri("/users/{username}",
                                    githubUsername)
                            .retrieve()
                            .body(Map.class);

            if (profile == null) {
                throw new IllegalArgumentException(
                        "GitHub user not found"
                );
            }

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "GitHub user not found"
            );
        }

        user.setGithubUsername(githubUsername);

        userRepository.save(user);

        return GitHubUsernameResponse.builder()
                .githubUsername(user.getGithubUsername())
                .build();
    }

    public GitHubRepositoriesResponse getRepositories() {

        User user = authService.getCurrentUser();

        String githubUsername = user.getGithubUsername();

        if (githubUsername == null || githubUsername.isBlank()) {
            throw new IllegalStateException(
                    "GitHub username not configured"
            );
        }

        List<Map<String, Object>> repos =
                githubRestClient.get()
                        .uri(
                                "/users/{username}/repos?per_page=100&sort=updated",
                                githubUsername
                        )
                        .retrieve()
                        .body(List.class);

        if (repos == null) {
            repos = List.of();
        }

        int totalStars = repos.stream()
                .mapToInt(repo ->
                        ((Number) repo.get("stargazers_count"))
                                .intValue()
                )
                .sum();

        List<GitHubRepositoryResponse> repositories =
                repos.stream()
                        .sorted((a, b) -> Integer.compare(
                                ((Number) b.get("stargazers_count")).intValue(),
                                ((Number) a.get("stargazers_count")).intValue()
                        ))
                        .limit(10)
                        .map(repo ->
                                GitHubRepositoryResponse.builder()
                                        .name((String) repo.get("name"))
                                        .description(
                                                (String) repo.get("description")
                                        )
                                        .language(
                                                (String) repo.get("language")
                                        )
                                        .stars(
                                                ((Number) repo.get("stargazers_count"))
                                                        .intValue()
                                        )
                                        .forks(
                                                ((Number) repo.get("forks_count"))
                                                        .intValue()
                                        )
                                        .repositoryUrl(
                                                (String) repo.get("html_url")
                                        )
                                        .build()
                        )
                        .toList();

        return GitHubRepositoriesResponse.builder()
                .totalRepositories(repos.size())
                .totalStars(totalStars)
                .repositories(repositories)
                .build();
    }
}