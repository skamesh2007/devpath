import ProtectedRoute from "@/components/auth/ProtectedRoute"
import Navbar from "@/components/navBar"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <ProtectedRoute>
      {children}
      <Navbar />
    </ProtectedRoute>
  )
}