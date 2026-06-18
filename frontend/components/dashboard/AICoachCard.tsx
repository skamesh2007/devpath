"use client"

import { Button } from "@/components/ui/button"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Sparkles } from "lucide-react"
import { AIInsightsResponse } from "@/types/ai"
import { useRouter } from "next/navigation"

type AICoachCardProps = {
  insightsLoading: boolean
  insights: AIInsightsResponse | null
  router: ReturnType<typeof useRouter>
  onGenerateInsights: () => void
}

export default function AICoachCard({
  insightsLoading,
  insights,
  router,
  onGenerateInsights,
}: AICoachCardProps) {
  return (
    <div className="grid gap-6 lg:grid-cols-3">
      {/* AI Career Coach */}
      <div className="lg:col-span-2">
        <Card className="h-full rounded-2xl">
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Sparkles className="h-5 w-5" />
                  AI Career Coach
                </CardTitle>

                <CardDescription>
                  Personalized guidance based on your progress.
                </CardDescription>
              </div>

              <Button
                size="sm"
                onClick={onGenerateInsights}
                disabled={insightsLoading || !!insights}
              >
                {insightsLoading
                  ? "Generating..."
                  : insights
                    ? "Generated"
                    : "Generate"}
              </Button>
            </div>
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
  )
}
