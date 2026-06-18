import api from "@/lib/api"

import { CareerMomentumResponse, DashboardResponse } from "@/types/dashboard"

export const getDashboard = async (): Promise<DashboardResponse> => {
  const response = await api.get("/dashboard")
  return response.data
}

export const getCareerMomentum = async (): Promise<CareerMomentumResponse> => {
  const response = await api.get("/dashboard/momentum")

  return response.data
}
