"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Target, CheckCircle2, Code2 } from "lucide-react"

import { DashboardResponse } from "@/services/dashboardService"


export default function CareerProgressCard({ dashboard }: { dashboard: DashboardResponse }) {
  return (
    <Card className="rounded-2xl">
      <CardHeader>
        <CardTitle>Career Progress</CardTitle>

        <CardDescription>Track your learning journey.</CardDescription>
      </CardHeader>

      <CardContent className="space-y-4">
        <div className="flex items-center justify-between rounded-xl border p-4">
          <div>
            <p className="text-sm text-muted-foreground">Roadmap Completion</p>

            <p className="text-2xl font-bold">
              {dashboard?.roadmapProgress ?? 0}%
            </p>
          </div>

          <Target className="h-8 w-8 text-muted-foreground" />
        </div>

        <div className="flex items-center justify-between rounded-xl border p-4">
          <div>
            <p className="text-sm text-muted-foreground">Tasks Completed</p>

            <p className="text-2xl font-bold">
              {dashboard?.completedTasks ?? 0}
            </p>
          </div>

          <CheckCircle2 className="h-8 w-8 text-muted-foreground" />
        </div>

        <div className="flex items-center justify-between rounded-xl border p-4">
          <div>
            <p className="text-sm text-muted-foreground">LeetCode Solved</p>

            <p className="text-2xl font-bold">
              {dashboard?.leetcodeSolved ?? 0}
            </p>
          </div>

          <Code2 className="h-8 w-8 text-muted-foreground" />
        </div>
      </CardContent>
    </Card>
  )
}
