"use client"

import { useEffect, useState } from "react"

import { useAuthStore } from "@/store/authStore"
import { DashboardResponse, getDashboard } from "@/services/dashboardService"

import { getInsights } from "@/services/aiService"
import { AIInsightsResponse } from "@/types/ai"

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"

import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"

import { useRouter } from "next/navigation"

import {
  BookOpen,
  CheckCircle2,
  Code2,
  Rocket,
  Sparkles,
  Target,
} from "lucide-react"

import DashboardLoading from "@/components/loading/DashboardLoading"

export default function DashboardPage() {
  const user = useAuthStore((state) => state.user)

  const router = useRouter()

  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null)
  const [insights, setInsights] = useState<AIInsightsResponse | null>(null)
  const [dashboardLoading, setDashboardLoading] = useState(true)
  const [insightsLoading, setInsightsLoading] = useState(true)

  useEffect(() => {
    getDashboard()
      .then(setDashboard)
      .catch(console.error)
      .finally(() => setDashboardLoading(false))

    getInsights()
      .then(setInsights)
      .catch(console.error)
      .finally(() => setInsightsLoading(false))
  }, [])

  const stats = dashboard
    ? [
        {
          title: "Roadmap Progress",
          value: `${dashboard.roadmapProgress}%`,
          description: `${dashboard.completedTasks} of ${dashboard.totalTasks} tasks completed`,
          icon: Target,
          progress: dashboard.roadmapProgress,
        },
        {
          title: "LeetCode Solved",
          value: dashboard.leetcodeSolved.toString(),
          description: "Problems solved",
          icon: Code2,
          progress: 100,
        },
        {
          title: "Completed Tasks",
          value: dashboard.completedTasks.toString(),
          description: `${dashboard.totalTasks} total tasks`,
          icon: CheckCircle2,
          progress:
            dashboard.totalTasks === 0
              ? 0
              : (dashboard.completedTasks * 100) / dashboard.totalTasks,
        },
        {
          title: "Active Projects",
          value: dashboard.activeProjects.toString(),
          description: "Projects in progress",
          icon: Rocket,
          progress: 0,
        },
      ]
    : []

  if (dashboardLoading) {
    return <DashboardLoading />
  }

  if (!dashboard && !dashboardLoading) {
    return <div className="p-6">Failed to load dashboard data.</div>
  }

  return (
    <div className="container mx-auto space-y-8 p-6">
      {/* Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div className="space-y-2">
          <div className="flex items-center gap-2">
            <Badge variant="secondary" className="rounded-full px-3 py-1">
              <Sparkles className="mr-1 h-3.5 w-3.5" />
              Dashboard
            </Badge>
          </div>

          <h1 className="text-3xl font-bold tracking-tight">
            Welcome, {user?.username ?? "User"}!
          </h1>

          <p className="text-sm text-muted-foreground">
            {user?.email ?? "your email"} • Your career progress at a glance
          </p>
        </div>

        <div className="flex gap-2">
          <Button variant="outline" onClick={() => router.push("/roadmap")}>
            <BookOpen className="mr-2 h-4 w-4" />
            View Roadmap
          </Button>

          <Button onClick={() => router.push("/roadmap/generate")}>
            <Sparkles className="mr-2 h-4 w-4" />
            Generate AI Roadmap
          </Button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon

          return (
            <Card key={stat.title} className="rounded-2xl">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  {stat.title}
                </CardTitle>

                <Icon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>

              <CardContent className="space-y-3">
                <div className="text-2xl font-bold">{stat.value}</div>

                <p className="text-xs text-muted-foreground">
                  {stat.description}
                </p>

                <Progress value={stat.progress} className="h-2" />
              </CardContent>
            </Card>
          )
        })}
      </div>

      <Card className="rounded-2xl">
        <CardHeader>
          <CardTitle>Continue Learning</CardTitle>

          <CardDescription>Pick up where you left off.</CardDescription>
        </CardHeader>

        <CardContent className="space-y-4">
          <div className="rounded-xl border p-4">
            <p className="font-medium">Your Current Roadmap</p>

            <p className="text-sm text-muted-foreground">
              {dashboard?.completedTasks ?? 0} of {dashboard?.totalTasks ?? 0}{" "}
              tasks completed
            </p>

            <Progress
              value={dashboard?.roadmapProgress ?? 0}
              className="mt-3"
            />
          </div>

          <Button className="w-full" onClick={() => router.push("/roadmap")}>
            Continue Roadmap
          </Button>
        </CardContent>
      </Card>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* AI Career Coach */}
        <div className="lg:col-span-2">
          <Card className="h-full rounded-2xl">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Sparkles className="h-5 w-5" />
                AI Career Coach
              </CardTitle>

              <CardDescription>
                Personalized guidance based on your progress.
              </CardDescription>
            </CardHeader>

            <CardContent className="space-y-4">
              {insightsLoading ? (
                <>
                  <div className="h-20 animate-pulse rounded-xl border bg-muted" />
                  <div className="h-20 animate-pulse rounded-xl border bg-muted" />
                </>
              ) : insights ? (
                <>
                  <div className="rounded-xl border p-4">
                    <p className="font-medium">Recommended Next Step</p>

                    <p className="mt-1 text-sm text-muted-foreground">
                      {insights.nextActions?.[0] ??
                        "No recommendations available yet."}
                    </p>
                  </div>

                  <div className="rounded-xl border p-4">
                    <p className="font-medium">Improvement Area</p>

                    <p className="mt-1 text-sm text-muted-foreground">
                      {insights.weaknesses?.[0] ??
                        "No weaknesses identified yet."}
                    </p>
                  </div>

                  <Button
                    variant="outline"
                    className="w-full"
                    onClick={() => router.push("/insights")}
                  >
                    View Full Analysis
                  </Button>
                </>
              ) : (
                <p className="text-sm text-muted-foreground">
                  No insights available yet.
                </p>
              )}
            </CardContent>
          </Card>
        </div>

        {/* AI Features */}
        <div>
          <Card className="h-full rounded-2xl">
            <CardHeader>
              <CardTitle>AI Features</CardTitle>
            </CardHeader>

            <CardContent className="space-y-3">
              <Button
                className="w-full"
                onClick={() => router.push("/roadmap/generate")}
              >
                Generate AI Roadmap
              </Button>

              <Button
                variant="outline"
                className="w-full"
                onClick={() => router.push("/insights")}
              >
                View AI Insights
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Career Progress */}
      <Card className="rounded-2xl">
        <CardHeader>
          <CardTitle>Career Progress</CardTitle>

          <CardDescription>Track your learning journey.</CardDescription>
        </CardHeader>

        <CardContent className="space-y-4">
          <div className="flex items-center justify-between rounded-xl border p-4">
            <div>
              <p className="text-sm text-muted-foreground">
                Roadmap Completion
              </p>

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
    </div>
  )
}
