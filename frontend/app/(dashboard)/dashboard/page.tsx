"use client";

import { useAuthStore } from "@/store/authStore";


export default function DashboardPage() {
  const user = useAuthStore((state) => state.user);

  return (
    <div className="container mx-auto p-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Welcome, {user?.username}!</h1>
          <p className="text-sm text-muted-foreground">{user?.email}</p>
        </div>
      </div>
    </div>
  );
}