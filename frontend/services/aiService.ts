import api from "@/lib/api";
import {
  GenerateRoadmapRequest,
  AIRoadmapResponse,
  AIInsightsResponse,
} from "@/types/ai";

export const generateRoadmap = async (
  data: GenerateRoadmapRequest
): Promise<AIRoadmapResponse> => {
  const response = await api.post(
    "/ai/roadmaps/generate",
    data
  );

  return response.data;
};

export const getInsights =
  async (): Promise<AIInsightsResponse> => {
    const response = await api.get("/ai/insights");

    return response.data;
  };