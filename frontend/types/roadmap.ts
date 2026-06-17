export interface Roadmap {
  id: number;
  title: string;
  description: string;
  totalTasks: number;
  completedTasks: number;
  progress: number;
}

export interface RoadmapTask {
  id: number;
  title: string;
  description: string;
  completed: boolean;
}

export interface CreateRoadmapRequest {
  title: string;
  description: string;
}

export interface UpdateRoadmapRequest {
  title: string;
  description: string;
}