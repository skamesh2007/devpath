import api from "@/lib/api";

export interface DashboardResponse {
  roadmapProgress: number;
  completedTasks: number;
  totalTasks: number;
  activeProjects: number;
  leetcodeSolved: number;
}

export const getDashboard = async (): Promise<DashboardResponse> => {
  const response = await api.get("/dashboard");
  return response.data;
};