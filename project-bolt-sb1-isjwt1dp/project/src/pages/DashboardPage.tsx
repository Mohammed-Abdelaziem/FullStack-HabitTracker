import React from "react";
import { useQuery } from "@tanstack/react-query";
import { Target, Play, CheckCircle, TrendingUp } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { apiClient } from "../utils/api";
import { LoadingSpinner } from "../components/ui/LoadingSpinner";

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  const { data: habits = [], isLoading: habitsLoading } = useQuery({
    queryKey: ["habits"],
    queryFn: apiClient.getHabits,
  });

  const { data: ongoingHabits = [], isLoading: ongoingLoading } = useQuery({
    queryKey: ["ongoing-habits"],
    queryFn: () => apiClient.getOngoingHabits(),
  });

  const isLoading = habitsLoading || ongoingLoading;

  const totalHabits = habits.length;
  const completedHabits = ongoingHabits.filter(
    (h) => h.status === "COMPLETED"
  ).length;
  const inProgressHabits = ongoingHabits.filter(
    (h) => h.status === "ACTIVE"
  ).length;
  const overallProgress =
    totalHabits > 0 ? (completedHabits / totalHabits) * 100 : 0;

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
          Welcome back, {user?.name}!
        </h1>
        <p className="mt-2 text-gray-600 dark:text-gray-400">
          Here's your habit tracking overview for today.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white dark:bg-gray-800 overflow-hidden shadow-sm rounded-xl border border-gray-200 dark:border-gray-700">
          <div className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <Target className="h-8 w-8 text-blue-600" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 dark:text-gray-400 truncate">
                    Total Habits
                  </dt>
                  <dd className="text-2xl font-bold text-gray-900 dark:text-white">
                    {totalHabits}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 overflow-hidden shadow-sm rounded-xl border border-gray-200 dark:border-gray-700">
          <div className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <CheckCircle className="h-8 w-8 text-green-600" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 dark:text-gray-400 truncate">
                    Completed
                  </dt>
                  <dd className="text-2xl font-bold text-gray-900 dark:text-white">
                    {completedHabits}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 overflow-hidden shadow-sm rounded-xl border border-gray-200 dark:border-gray-700">
          <div className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <Play className="h-8 w-8 text-amber-600" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 dark:text-gray-400 truncate">
                    In Progress
                  </dt>
                  <dd className="text-2xl font-bold text-gray-900 dark:text-white">
                    {inProgressHabits}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 overflow-hidden shadow-sm rounded-xl border border-gray-200 dark:border-gray-700">
          <div className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <TrendingUp className="h-8 w-8 text-purple-600" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 dark:text-gray-400 truncate">
                    Overall Progress
                  </dt>
                  <dd className="text-2xl font-bold text-gray-900 dark:text-white">
                    {overallProgress.toFixed(1)}%
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Recent Habits
          </h3>
          {habits.length > 0 ? (
            <div className="space-y-3">
              {habits.slice(0, 3).map((habit) => (
                <div
                  key={habit.id}
                  className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg"
                >
                  <div>
                    <p className="font-medium text-gray-900 dark:text-white">
                      {habit.name}
                    </p>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {habit.frequency}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-500 dark:text-gray-400 text-center py-8">
              No habits yet. Create your first habit to get started!
            </p>
          )}
        </div>

        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            In Progress Ongoing Habits
          </h3>
          {ongoingHabits.filter((h) => h.status === "IN_PROGRESS").length >
          0 ? (
            <div className="space-y-3">
              {ongoingHabits
                .filter((h) => h.status === "IN_PROGRESS")
                .slice(0, 3)
                .map((ongoingHabit) => (
                  <div
                    key={ongoingHabit.ongoingHabitId}
                    className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg"
                  >
                    <div>
                      <p className="font-medium text-gray-900 dark:text-white">
                        {ongoingHabit.habit?.name || "Habit"}
                      </p>
                      <p className="text-sm text-gray-600 dark:text-gray-400">
                        Active
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm font-medium text-gray-900 dark:text-white">
                        {ongoingHabit.counter} / {ongoingHabit.target}
                      </p>
                      <div className="w-20 bg-gray-200 dark:bg-gray-600 rounded-full h-1.5 mt-1">
                        <div
                          className="bg-green-600 h-1.5 rounded-full"
                          style={{
                            width: `${Math.min(
                              (ongoingHabit.counter / ongoingHabit.target) *
                                100,
                              100
                            )}%`,
                          }}
                        />
                      </div>
                    </div>
                  </div>
                ))}
            </div>
          ) : (
            <p className="text-gray-500 dark:text-gray-400 text-center py-8">
              No In Progress ongoing habits. Start tracking some habits!
            </p>
          )}
        </div>
      </div>
    </div>
  );
};
