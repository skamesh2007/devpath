"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { SiLeetcode } from "react-icons/si"
import { ExternalLink, Settings2 } from "lucide-react"
import { getMyLeetCodeStats, getLeetCodeUsername } from "@/services/leetcodeService"
import { LeetCodeStatsResponse } from "@/types/leetcode"

import LeetCodeLoading from "@/components/loading/LeetCodeLoading"

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
    <div className="flex flex-col items-center justify-center rounded-2xl border bg-card p-3 shadow-sm sm:p-5">
      <span className={`mb-1 text-[10px] font-semibold uppercase tracking-widest sm:text-xs ${color}`}>
        {label}
      </span>
      <span className="text-2xl font-bold sm:text-3xl">{count}</span>
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
    return <LeetCodeLoading />
  }

  // ── Not connected ──────────────────────────────────────────────────────────

  if (!hasLinked) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center gap-6 p-4 text-center sm:p-6">
        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-muted sm:h-20 sm:w-20">
          <SiLeetcode className="h-8 w-8 text-muted-foreground sm:h-10 sm:w-10" />
        </div>

        <div className="space-y-1">
          <h1 className="text-xl font-bold sm:text-2xl">LeetCode not connected</h1>
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
    <div className="mx-auto max-w-2xl space-y-6 p-4 pb-24 sm:p-6 sm:pb-28">

      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="flex items-center gap-3 min-w-0">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-[#FFA116]/10">
            <SiLeetcode className="h-6 w-6 text-[#FFA116]" />
          </div>
          <div className="min-w-0">
            <h1 className="truncate text-xl font-bold sm:text-2xl">{stats?.username}</h1>
            <p className="text-sm text-muted-foreground">
              Global Rank&nbsp;
              <span className="font-semibold text-foreground">
                #{stats?.ranking}
              </span>
            </p>
          </div>
        </div>

        {/* Link to LeetCode profile */}
        <Link
          href={`https://leetcode.com/${stats?.username}`}
          target="_blank"
          rel="noopener noreferrer"
          className="flex items-center justify-center gap-1.5 self-start rounded-xl border px-3 py-1.5 text-xs font-medium text-muted-foreground transition hover:bg-muted sm:self-auto"
        >
          <ExternalLink className="h-3.5 w-3.5" />
          View on LeetCode
        </Link>
      </div>

      {/* Solve counts */}
      <div>
        <p className="mb-3 text-xs font-semibold uppercase tracking-widest text-muted-foreground">
          Problems Solved
        </p>
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
          <div className="col-span-2 flex flex-col items-center justify-center rounded-2xl border bg-card p-3 shadow-sm sm:col-span-1 sm:p-5">
            <span className="mb-1 text-[10px] font-semibold uppercase tracking-widest text-muted-foreground sm:text-xs">
              Total
            </span>
            <span className="text-2xl font-bold sm:text-3xl">{stats?.totalSolved}</span>
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
                  className="flex items-center justify-between gap-3 border-b px-3 py-3 last:border-b-0 hover:bg-muted/40 transition sm:gap-4 sm:px-4"
                >
                  <div className="min-w-0">
                    <Link
                      href={`https://leetcode.com/problems/${sub.titleSlug}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="block truncate font-medium hover:underline"
                    >
                      {sub.title}
                    </Link>
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