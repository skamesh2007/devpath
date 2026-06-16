"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { SiLeetcode } from "react-icons/si"
import { ExternalLink, Settings2 } from "lucide-react"
import { getMyLeetCodeStats, getLeetCodeUsername } from "@/services/leetcodeService"
import { LeetCodeStatsResponse } from "@/types/leetcode"

// ─── Difficulty badge ─────────────────────────────────────────────────────────

function DiffBadge({
  label,
  count,
  color,
}: {
  label: string
  count: number
  color: string
}) {
  return (
    <div className="flex flex-col items-center justify-center rounded-2xl border bg-card p-5 shadow-sm">
      <span className={`mb-1 text-xs font-semibold uppercase tracking-widest ${color}`}>
        {label}
      </span>
      <span className="text-3xl font-bold">{count}</span>
    </div>
  )
}

// ─── Page ─────────────────────────────────────────────────────────────────────

export default function LeetCodePage() {
  const router = useRouter()

  const [stats, setStats] = useState<LeetCodeStatsResponse | null>(null)
  const [hasLinked, setHasLinked] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true)
        const usernameRes = await getLeetCodeUsername()

        if (!usernameRes.username) {
          setHasLinked(false)
          return
        }

        setHasLinked(true)
        const statsRes = await getMyLeetCodeStats()
        setStats(statsRes)
      } catch {
        setHasLinked(false)
        setStats(null)
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [])

  // ── Loading ────────────────────────────────────────────────────────────────

  if (loading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <div className="h-6 w-6 animate-spin rounded-full border-2 border-foreground border-t-transparent" />
      </div>
    )
  }

  // ── Not connected ──────────────────────────────────────────────────────────

  if (!hasLinked) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center gap-6 p-6 text-center">
        <div className="flex h-20 w-20 items-center justify-center rounded-full bg-muted">
          <SiLeetcode className="h-10 w-10 text-muted-foreground" />
        </div>

        <div className="space-y-1">
          <h1 className="text-2xl font-bold">LeetCode not connected</h1>
          <p className="text-sm text-muted-foreground">
            Link your LeetCode account to track your progress here.
          </p>
        </div>

        <button
          onClick={() => router.push("/profile/edit")}
          className="flex items-center gap-2 rounded-xl bg-foreground px-5 py-2.5 text-sm font-medium text-background transition hover:opacity-90"
        >
          <Settings2 className="h-4 w-4" />
          Connect in Edit Profile
        </button>
      </div>
    )
  }

  // ── Stats ──────────────────────────────────────────────────────────────────

  return (
    <div className="mx-auto max-w-2xl space-y-6 p-6 pb-28">

      {/* Header */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-[#FFA116]/10">
            <SiLeetcode className="h-6 w-6 text-[#FFA116]" />
          </div>
          <div>
            <h1 className="text-2xl font-bold">{stats?.username}</h1>
            <p className="text-sm text-muted-foreground">
              Global Rank&nbsp;
              <span className="font-semibold text-foreground">
                #{stats?.ranking}
              </span>
            </p>
          </div>
        </div>

        {/* Link to LeetCode profile */}
        <a
          href={`https://leetcode.com/${stats?.username}`}
          target="_blank"
          rel="noopener noreferrer"
          className="flex items-center gap-1.5 rounded-xl border px-3 py-1.5 text-xs font-medium text-muted-foreground transition hover:bg-muted"
        >
          <ExternalLink className="h-3.5 w-3.5" />
          View on LeetCode
        </a>
      </div>

      {/* Solve counts */}
      <div>
        <p className="mb-3 text-xs font-semibold uppercase tracking-widest text-muted-foreground">
          Problems Solved
        </p>
        <div className="grid grid-cols-4 gap-3">
          <div className="col-span-4 flex flex-col items-center justify-center rounded-2xl border bg-card p-5 shadow-sm sm:col-span-1">
            <span className="mb-1 text-xs font-semibold uppercase tracking-widest text-muted-foreground">
              Total
            </span>
            <span className="text-3xl font-bold">{stats?.totalSolved}</span>
          </div>
          <DiffBadge label="Easy"   count={stats?.easySolved   ?? 0} color="text-emerald-500" />
          <DiffBadge label="Medium" count={stats?.mediumSolved ?? 0} color="text-amber-500"   />
          <DiffBadge label="Hard"   count={stats?.hardSolved   ?? 0} color="text-red-500"     />
        </div>
      </div>

      {/* Recent submissions */}
      {stats?.recentSubmissions && stats.recentSubmissions.length > 0 && (
        <div>
          <p className="mb-3 text-xs font-semibold uppercase tracking-widest text-muted-foreground">
            Recent Submissions
          </p>

          <div className="overflow-hidden rounded-2xl border">
            {stats.recentSubmissions.map((sub, i) => {
              const accepted = sub.statusDisplay === "Accepted"
              return (
                <div
                  key={i}
                  className="flex items-center justify-between gap-4 border-b px-4 py-3 last:border-b-0 hover:bg-muted/40 transition"
                >
                  <div className="min-w-0">
                    <a
                      href={`https://leetcode.com/problems/${sub.titleSlug}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="truncate font-medium hover:underline"
                    >
                      {sub.title}
                    </a>
                    <p className="text-xs text-muted-foreground">{sub.lang}</p>
                  </div>

                  <span
                    className={`shrink-0 rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                      accepted
                        ? "bg-emerald-100 text-emerald-700 dark:bg-emerald-900/40 dark:text-emerald-400"
                        : "bg-red-100 text-red-600 dark:bg-red-900/40 dark:text-red-400"
                    }`}
                  >
                    {sub.statusDisplay}
                  </span>
                </div>
              )
            })}
          </div>
        </div>
      )}

      {/* Manage link */}
      <div className="flex justify-center">
        <button
          onClick={() => router.push("/profile/edit")}
          className="flex items-center gap-1.5 text-xs text-muted-foreground transition hover:text-foreground"
        >
          <Settings2 className="h-3.5 w-3.5" />
          Change linked account in Edit Profile
        </button>
      </div>

    </div>
  )
}