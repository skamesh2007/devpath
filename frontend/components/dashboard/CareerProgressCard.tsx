"use client"

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Target, CheckCircle2, Code2 } from "lucide-react"

import { DashboardResponse } from "@/types/dashboard"

const colorClasses = {
  blue: { bg: "bg-blue-500/10", text: "text-blue-500" },
  emerald: { bg: "bg-emerald-500/10", text: "text-emerald-500" },
  amber: { bg: "bg-amber-500/10", text: "text-amber-500" },
} as const

export default function CareerProgressCard({
  dashboard,
}: {
  dashboard: DashboardResponse
}) {
  const metrics = [
    {
      label: "Roadmap Completion",
      value: `${dashboard?.roadmapProgress ?? 0}%`,
      icon: Target,
      color: "blue" as const,
      progress: dashboard?.roadmapProgress ?? 0,
    },
    {
      label: "Tasks Completed",
      value: dashboard?.completedTasks ?? 0,
      icon: CheckCircle2,
      color: "emerald" as const,
    },
    {
      label: "LeetCode Solved",
      value: dashboard?.leetcodeSolved ?? 0,
      icon: Code2,
      color: "amber" as const,
    },
  ]

  return (
    <Card className="rounded-2xl">
      <CardHeader>
        <CardTitle>Career Progress</CardTitle>
        <CardDescription>Track your learning journey.</CardDescription>
      </CardHeader>

      <CardContent className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        {metrics.map((metric) => {
          const Icon = metric.icon
          const colors = colorClasses[metric.color]

          return (
            <div key={metric.label} className="rounded-xl border p-4">
              <div className="flex items-center justify-between">
                <p className="text-sm text-muted-foreground">
                  {metric.label}
                </p>
                <div
                  className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-lg ${colors.bg}`}
                >
                  <Icon className={`h-4 w-4 ${colors.text}`} />
                </div>
              </div>

              <p className="mt-2 text-2xl font-bold">{metric.value}</p>

              {metric.progress !== undefined && (
                <Progress value={metric.progress} className="mt-3 h-1.5" />
              )}
            </div>
          )
        })}
      </CardContent>
    </Card>
  )
}