export interface GitHubUsernameResponse {
  githubUsername: string | null;
}

export interface GitHubStatsResponse {
  username: string;
  profileUrl: string;
  avatarUrl: string;

  publicRepos: number;
  followers: number;
  following: number;

  totalStars: number;
}

export interface GitHubRepositoryResponse {
  name: string
  description: string | null
  language: string | null

  stars: number
  forks: number

  repositoryUrl: string
}

export interface GitHubRepositoriesResponse {
  totalRepositories: number
  totalStars: number

  repositories: GitHubRepositoryResponse[]
}