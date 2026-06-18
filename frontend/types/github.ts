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