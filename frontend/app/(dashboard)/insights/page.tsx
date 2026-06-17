"use client";

import { useEffect, useState } from "react";

import { getInsights } from "@/services/aiService";
import { AIInsightsResponse } from "@/types/ai";

import { Card, CardContent } from "@/components/ui/card";

export default function InsightsPage() {
  const [insights, setInsights] =
    useState<AIInsightsResponse | null>(null);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {
    loadInsights();
  }, []);

  const loadInsights = async () => {
    try {
      const data = await getInsights();
      setInsights(data);
    } catch {
      setError("Failed to load AI insights");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <p>Loading AI insights...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto p-6">
        <p className="text-red-500">{error}</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      <h1 className="text-3xl font-bold">
        AI Insights
      </h1>

      <Card>
        <CardContent className="pt-6">
          <h2 className="text-xl font-semibold mb-4">
            Strengths
          </h2>

          <ul className="space-y-2">
            {insights?.strengths.map(
              (item, index) => (
                <li key={index}>
                  ✅ {item}
                </li>
              )
            )}
          </ul>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="pt-6">
          <h2 className="text-xl font-semibold mb-4">
            Weaknesses
          </h2>

          <ul className="space-y-2">
            {insights?.weaknesses.map(
              (item, index) => (
                <li key={index}>
                  ⚠️ {item}
                </li>
              )
            )}
          </ul>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="pt-6">
          <h2 className="text-xl font-semibold mb-4">
            Next Actions
          </h2>

          <ul className="space-y-2">
            {insights?.nextActions.map(
              (item, index) => (
                <li key={index}>
                  🚀 {item}
                </li>
              )
            )}
          </ul>
        </CardContent>
      </Card>
    </div>
  );
}