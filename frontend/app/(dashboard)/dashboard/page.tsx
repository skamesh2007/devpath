"use client";

import { useEffect, useState } from "react";

import { useAuthStore } from "@/store/authStore";
import { DashboardResponse, getDashboard } from "@/services/dashboardService";

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";

import { useRouter } from "next/navigation";

import {
  ArrowUpRight,
  BookOpen,
  CheckCircle2,
  Code2,
  Rocket,
  Sparkles,
  Target,
} from "lucide-react";

export default function DashboardPage() {
  const user = useAuthStore((state) => state.user);

  const router = useRouter();

  const [dashboard, setDashboard] =
    useState<DashboardResponse | null>(null);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        const data = await getDashboard();
        setDashboard(data);
      } catch (error) {
        console.error("Failed to load dashboard", error);
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

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
              : (dashboard.completedTasks * 100) /
                dashboard.totalTasks,
        },
        {
          title: "Active Projects",
          value: dashboard.activeProjects.toString(),
          description: "Projects in progress",
          icon: Rocket,
          progress: 0,
        },
      ]
    : [];

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <p className="text-muted-foreground">
          Loading dashboard...
        </p>
      </div>
    );
  }

  return (
    <div className="container mx-auto space-y-8 p-6">
      {/* Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div className="space-y-2">
          <div className="flex items-center gap-2">
            <Badge
              variant="secondary"
              className="rounded-full px-3 py-1"
            >
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

          <Button>
            <ArrowUpRight className="mr-2 h-4 w-4" />
            Continue Learning
          </Button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;

          return (
            <Card
              key={stat.title}
              className="rounded-2xl"
            >
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  {stat.title}
                </CardTitle>

                <Icon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>

              <CardContent className="space-y-3">
                <div className="text-2xl font-bold">
                  {stat.value}
                </div>

                <p className="text-xs text-muted-foreground">
                  {stat.description}
                </p>

                <Progress
                  value={stat.progress}
                  className="h-2"
                />
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* Overview + Actions */}
      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="rounded-2xl lg:col-span-2">
          <CardHeader>
            <CardTitle>Career Progress Overview</CardTitle>

            <CardDescription>
              Live data from your roadmap and LeetCode activity.
            </CardDescription>
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
                <p className="text-sm text-muted-foreground">
                  Tasks Completed
                </p>

                <p className="text-2xl font-bold">
                  {dashboard?.completedTasks ?? 0}
                </p>
              </div>

              <CheckCircle2 className="h-8 w-8 text-muted-foreground" />
            </div>

            <div className="flex items-center justify-between rounded-xl border p-4">
              <div>
                <p className="text-sm text-muted-foreground">
                  LeetCode Solved
                </p>

                <p className="text-2xl font-bold">
                  {dashboard?.leetcodeSolved ?? 0}
                </p>
              </div>

              <Code2 className="h-8 w-8 text-muted-foreground" />
            </div>
          </CardContent>
        </Card>

        <Card className="rounded-2xl">
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>

            <CardDescription>
              Continue building your profile.
            </CardDescription>
          </CardHeader>

          <CardContent className="space-y-3">
            <Button className="w-full">
              Create Roadmap
            </Button>

            <Button
              variant="outline"
              className="w-full"
            >
              Add Task
            </Button>

            <Button
              variant="outline"
              className="w-full"
            >
              Open AI Mentor
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}