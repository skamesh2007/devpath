"use client"

import { useEffect, useState } from "react"

import { getRoadmaps } from "@/services/roadmapService"
import { getTasks, updateTask } from "@/services/taskService"

import { Roadmap, RoadmapTask } from "@/types/roadmap"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

import { Progress } from "@/components/ui/progress"

import CreateRoadmapDialog from "@/components/CreateRoadmapDialog"
import CreateTaskDialog from "@/components/CreateTaskDialog"

import { Trash2 } from "lucide-react"
import { deleteRoadmap } from "@/services/roadmapService"
import { deleteTask } from "@/services/taskService"
import DeleteConfirmDialog from "@/components/DeleteConfirmDialog"

import { Checkbox } from "@/components/ui/checkbox"

import EditRoadmapDialog from "@/components/EditRoadmapDialog"
import EditTaskDialog from "@/components/EditTaskDialog"

export default function RoadmapPage() {
  const [roadmaps, setRoadmaps] = useState<Roadmap[]>([])
  const [selectedRoadmap, setSelectedRoadmap] = useState<Roadmap | null>(null)

  const [tasks, setTasks] = useState<RoadmapTask[]>([])

  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadRoadmaps()
  }, [])

  const loadRoadmaps = async () => {
    try {
      const data = await getRoadmaps()

      setRoadmaps(data)

      if (data.length > 0) {
        setSelectedRoadmap(data[0])
        await loadTasks(data[0].id)
      }
    } catch (error) {
      console.error("Failed to load roadmaps", error)
    } finally {
      setLoading(false)
    }
  }

  const loadTasks = async (roadmapId: number) => {
    try {
      const data = await getTasks(roadmapId)
      setTasks(data)
    } catch (error) {
      console.error("Failed to load tasks", error)
    }
  }

  const selectRoadmap = async (roadmap: Roadmap) => {
    setSelectedRoadmap(roadmap)
    await loadTasks(roadmap.id)
  }

  const toggleTask = async (taskId: number, completed: boolean) => {
    try {
      await updateTask(taskId, {
        completed,
      })

      if (!selectedRoadmap) return

      await loadTasks(selectedRoadmap.id)

      const updatedRoadmaps = await getRoadmaps()

      setRoadmaps(updatedRoadmaps)

      const updatedRoadmap = updatedRoadmaps.find(
        (r) => r.id === selectedRoadmap.id
      )

      if (updatedRoadmap) {
        setSelectedRoadmap(updatedRoadmap)
      }
    } catch (error) {
      console.error("Failed to update task", error)
    }
  }

  const handleDeleteRoadmap = async (roadmapId: number) => {
    try {
      await deleteRoadmap(roadmapId)

      const updatedRoadmaps = await getRoadmaps()

      setRoadmaps(updatedRoadmaps)

      if (updatedRoadmaps.length > 0) {
        setSelectedRoadmap(updatedRoadmaps[0])

        await loadTasks(updatedRoadmaps[0].id)
      } else {
        setSelectedRoadmap(null)
        setTasks([])
      }
    } catch (error) {
      console.error("Failed to delete roadmap", error)
    }
  }

  const handleDeleteTask = async (taskId: number) => {
    try {
      await deleteTask(taskId)

      if (!selectedRoadmap) return

      await loadTasks(selectedRoadmap.id)

      const updatedRoadmaps = await getRoadmaps()

      setRoadmaps(updatedRoadmaps)

      const updated = updatedRoadmaps.find((r) => r.id === selectedRoadmap.id)

      if (updated) {
        setSelectedRoadmap(updated)
      }
    } catch (error) {
      console.error("Failed to delete task", error)
    }
  }

  if (loading) {
    return (
      <div className="p-6">
        <p className="text-muted-foreground">Loading roadmaps...</p>
      </div>
    )
  }

  return (
    <div className="grid h-full gap-6 p-6 lg:grid-cols-3">
      {/* LEFT PANEL */}
      <Card className="lg:col-span-1">
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>Roadmaps</CardTitle>

            <CreateRoadmapDialog onCreated={loadRoadmaps} />
          </div>
        </CardHeader>

        <CardContent className="space-y-4">
          {roadmaps.length === 0 && (
            <div className="rounded-xl border p-4">
              <p className="text-sm text-muted-foreground">
                No roadmaps found.
              </p>
            </div>
          )}

          {roadmaps.map((roadmap) => (
            <button
              key={roadmap.id}
              onClick={() => selectRoadmap(roadmap)}
              className={`w-full rounded-xl border p-4 text-left transition hover:bg-muted ${
                selectedRoadmap?.id === roadmap.id ? "border-primary" : ""
              }`}
            >
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-medium">{roadmap.title}</h3>
                </div>

                <div className="flex items-center gap-3">
                  <EditRoadmapDialog
                    roadmapId={roadmap.id}
                    initialTitle={roadmap.title}
                    initialDescription={roadmap.description ?? ""}
                    onUpdated={loadRoadmaps}
                  />

                  <span className="text-sm text-muted-foreground">
                    {roadmap.progress}%
                  </span>

                  <DeleteConfirmDialog
                    title="Delete Roadmap"
                    description="This roadmap and all its tasks will be permanently deleted."
                    onConfirm={() => handleDeleteRoadmap(roadmap.id)}
                    trigger={
                      <Trash2
                        className="h-4 w-4 cursor-pointer text-muted-foreground hover:text-destructive"
                        onClick={(e) => e.stopPropagation()}
                      />
                    }
                  />
                </div>
              </div>

              <div className="mt-3">
                <Progress value={roadmap.progress} />
              </div>

              <p className="mt-2 text-xs text-muted-foreground">
                {roadmap.completedTasks}
                {" / "}
                {roadmap.totalTasks}
                {" tasks completed"}
              </p>
            </button>
          ))}
        </CardContent>
      </Card>

      {/* RIGHT PANEL */}
      <Card className="lg:col-span-2">
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>{selectedRoadmap?.title ?? "Tasks"}</CardTitle>

            {selectedRoadmap && (
              <CreateTaskDialog
                roadmapId={selectedRoadmap.id}
                onCreated={async () => {
                  await loadTasks(selectedRoadmap.id)

                  const updatedRoadmaps = await getRoadmaps()

                  setRoadmaps(updatedRoadmaps)

                  const updated = updatedRoadmaps.find(
                    (r) => r.id === selectedRoadmap.id
                  )

                  if (updated) {
                    setSelectedRoadmap(updated)
                  }
                }}
              />
            )}
          </div>
        </CardHeader>

        <CardContent className="space-y-3">
          {!selectedRoadmap && (
            <p className="text-sm text-muted-foreground">Select a roadmap.</p>
          )}

          {selectedRoadmap && tasks.length === 0 && (
            <p className="text-sm text-muted-foreground">No tasks available.</p>
          )}

          {tasks.map((task) => (
            <div key={task.id} className="rounded-xl border p-4">
              <div className="flex items-start justify-between">
                <div className="flex flex-1 items-start gap-3">
                  <Checkbox
                    checked={task.completed}
                    onCheckedChange={(checked) =>
                      toggleTask(task.id, Boolean(checked))
                    }
                  />

                  <div>
                    <p className="font-medium">{task.title}</p>

                    {task.description && (
                      <p className="text-sm text-muted-foreground">
                        {task.description}
                      </p>
                    )}
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <EditTaskDialog
                    taskId={task.id}
                    title={task.title}
                    description={task.description ?? ""}
                    completed={task.completed}
                    onUpdated={async () => {
                      if (selectedRoadmap) {
                        await loadTasks(selectedRoadmap.id)
                      }
                    }}
                  />

                  <DeleteConfirmDialog
                    title="Delete Task"
                    description="This task will be permanently deleted."
                    onConfirm={() => handleDeleteTask(task.id)}
                    trigger={
                      <Trash2 className="h-4 w-4 cursor-pointer text-muted-foreground hover:text-destructive" />
                    }
                  />
                </div>
              </div>
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  )
}
