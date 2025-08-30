import React, { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
} from "recharts";
import { Calendar, Filter } from "lucide-react";
import { apiClient } from "../utils/api";
import { LoadingSpinner } from "../components/ui/LoadingSpinner";

export interface ProgressData {
  progressPercentage: string; // "0%" from API
  habitName?: string; // optional, if API provides
  currentStreak?: number; // optional, if API provides later
  longestStreak?: number; // optional, if API provides later
}

export const ProgressPage: React.FC = () => {
  const [filters, setFilters] = useState<Record<string, string>>({});

  // Fetch progress data
  const { data: progressData = null, isLoading } = useQuery({
    queryKey: ["progress", filters],
    queryFn: () => apiClient.getProgress(filters),
  });

  // Fetch habits and ongoing habits
  const { data: habits = [] } = useQuery({
    queryKey: ["habits"],
    queryFn: apiClient.getHabits,
  });

  const { data: ongoingHabits = [] } = useQuery({
    queryKey: ["ongoing-habits"],
    queryFn: apiClient.getOngoingHabits,
  });

  const isLoadingAll = isLoading || !habits || !ongoingHabits;
  if (isLoadingAll) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }
  if (progressData) {
    const completionValue = parseFloat(
      progressData.progressPercentage.replace("%", "")
    );
    const habitName = progressData.habitName ?? "Habit";
    const chartData = [
      {
        name: habitName,
        completion: completionValue,
      },
    ];
  }

  // Prepare pie chart data
  const statusData = [
    {
      name: "Active",
      value: ongoingHabits.filter((h) => h.status === "ACTIVE").length,
      color: "#10B981",
    },
    {
      name: "Completed",
      value: ongoingHabits.filter((h) => h.status === "COMPLETED").length,
      color: "#3B82F6",
    },
    {
      name: "Paused",
      value: ongoingHabits.filter((h) => h.status === "PAUSED").length,
      color: "#F59E0B",
    },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
            Progress
          </h1>
          <p className="mt-2 text-gray-600 dark:text-gray-400">
            Track your habit completion and streaks
          </p>
        </div>
        <div className="flex items-center space-x-4">
          <button className="inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-lg text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
            <Filter className="h-4 w-4 mr-2" />
            Filter
          </button>
          <button className="inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-lg text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
            <Calendar className="h-4 w-4 mr-2" />
            Date Range
          </button>
        </div>
      </div>
      {/* Progress Table */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-6">
          Detailed Progress
        </h3>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-700">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Habit
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Completion
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Current Streak
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Longest Streak
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {progressData ? (
                <tr className="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                    Habit
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <span className="text-sm text-gray-900 dark:text-white mr-2">
                        {parseFloat(progressData.progressPercentage).toFixed(1)}
                        %
                      </span>
                      <div className="w-16 bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                        <div
                          className="bg-blue-600 h-2 rounded-full"
                          style={{
                            width: `${parseFloat(
                              progressData.progressPercentage
                            )}%`,
                          }}
                        />
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                    {progressData.currentStreak || 0} days
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                    {progressData.longestStreak || 0} days
                  </td>
                </tr>
              ) : (
                <tr>
                  <td colSpan={4} className="text-center py-4">
                    No progress data available
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
