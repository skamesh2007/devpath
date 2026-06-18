"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

import { Progress } from "@/components/ui/progress"

import { Badge } from "@/components/ui/badge"

import { CareerMomentumResponse } from "@/types/dashboard"

interface CareerMomentumCardProps {
  momentum: CareerMomentumResponse
}

const getMomentumStatus = (score: number) => {
  if (score < 40) {
    return {
      label: "Poor",
      badgeClass: "bg-red-100 text-red-700 hover:bg-red-100",
      progressClass: "[&>div]:bg-red-500",
      message: "Your consistency is low. Focus on completing tasks regularly.",
    }
  }

  if (score < 70) {
    return {
      label: "Moderate",
      badgeClass: "bg-yellow-100 text-yellow-700 hover:bg-yellow-100",
      progressClass: "[&>div]:bg-yellow-500",
      message:
        "You're making progress, but there is room for more consistency.",
    }
  }

  return {
    label: "Excellent",
    badgeClass: "bg-green-100 text-green-700 hover:bg-green-100",
    progressClass: "[&>div]:bg-green-500",
    message: "Strong momentum. Maintain your current pace.",
  }
}

export default function CareerMomentumCard({
  momentum,
}: CareerMomentumCardProps) {
  const status = getMomentumStatus(momentum.momentumScore)

  return (
    <Card>
      <CardHeader>
        <CardTitle>Career Momentum</CardTitle>
      </CardHeader>

      <CardContent className="space-y-6">
        <div>
          <div className="mb-2 flex items-center justify-between">
            <span className="text-sm text-muted-foreground">
              Momentum Score
            </span>

            <div className="flex items-center gap-2">
              <Badge className={status.badgeClass}>{status.label}</Badge>

              <span className="font-bold">{momentum.momentumScore}/100</span>
            </div>
          </div>

          <Progress
            value={momentum.momentumScore}
            className={status.progressClass}
          />
          <p className="mt-2 text-sm text-muted-foreground">{status.message}</p>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <div className="rounded-lg border p-4">
            <p className="text-sm text-muted-foreground">Strongest Roadmap</p>

            <p className="font-semibold">{momentum.strongestRoadmap}</p>

            <p className="text-sm">
              {momentum.strongestRoadmapProgress}% complete
            </p>
          </div>

          <div className="rounded-lg border p-4">
            <p className="text-sm text-muted-foreground">Weakest Roadmap</p>

            <p className="font-semibold">{momentum.weakestRoadmap}</p>

            <p className="text-sm">
              {momentum.weakestRoadmapProgress}% complete
            </p>
          </div>

          <div className="rounded-lg border p-4">
            <p className="text-sm text-muted-foreground">Overdue Tasks</p>

            <p className="text-2xl font-bold">{momentum.totalOverdueTasks}</p>
          </div>

          <div className="rounded-lg border p-4">
            <p className="text-sm text-muted-foreground">Closest To Finish</p>

            <p className="font-semibold">{momentum.nearestCompletionRoadmap}</p>

            <p className="text-sm">
              {momentum.nearestCompletionPercentage}% complete
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
