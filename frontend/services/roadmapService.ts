import api from "@/lib/api";

import { Roadmap, CreateRoadmapRequest, UpdateRoadmapRequest } from "@/types/roadmap";



export const getRoadmaps = async (): Promise<Roadmap[]> => {
  const response = await api.get("/roadmaps");
  return response.data;
};

export const getRoadmap = async (
  id: number
): Promise<Roadmap> => {
  const response = await api.get(`/roadmaps/${id}`);
  return response.data;
};

export const createRoadmap = async (
  data: CreateRoadmapRequest
): Promise<Roadmap> => {
  const response = await api.post("/roadmaps", data);
  return response.data;
};

export const updateRoadmap = async (
  roadmapId: number,
  data: {
    title: string;
    description: string;
  }
) => {
  const response = await api.put(
    `/roadmaps/${roadmapId}`,
    data
  );

  return response.data;
};

export const deleteRoadmap = async (
  roadmapId: number
): Promise<void> => {
  await api.delete(`/roadmaps/${roadmapId}`);
};

