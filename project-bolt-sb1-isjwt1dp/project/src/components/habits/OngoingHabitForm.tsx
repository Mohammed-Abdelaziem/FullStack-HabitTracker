import React, { useState } from "react";
import type {
  OngoingHabit,
  CreateOngoingHabitRequest,
  Habit,
} from "../../types";
import { Target } from "lucide-react";

interface OngoingHabitFormProps {
  ongoingHabit?: OngoingHabit;
  habits: Habit[];
  onSubmit: (data: CreateOngoingHabitRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
}

export const OngoingHabitForm: React.FC<OngoingHabitFormProps> = ({
  ongoingHabit,
  habits,
  onSubmit,
  onCancel,
  isLoading,
}) => {
  const [formData, setFormData] = useState({
    habitId: ongoingHabit?.habitId || "",
    startDate:
      ongoingHabit?.startDate || new Date().toISOString().split("T")[0], // string "YYYY-MM-DD"
    count: ongoingHabit?.counter || 0,
    status: ongoingHabit?.status || "ACTIVE",
    target: ongoingHabit?.habit?.target || 1,
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      habitId: formData.habitId,
      startDate: formData.startDate,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {!ongoingHabit && (
        <div>
          <label
            htmlFor="habitId"
            className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
          >
            Select Habit
          </label>
          <select
            id="habitId"
            value={formData.habitId}
            onChange={(e) =>
              setFormData({ ...formData, habitId: e.target.value })
            }
            className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
            required
          >
            <option value="">Choose a habit</option>
            {habits.map((habit) => (
              <option key={habit.id} value={habit.id}>
                {habit.name}
              </option>
            ))}
          </select>
        </div>
      )}

      <div>
        <label
          htmlFor="startDate"
          className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
        >
          Start Date
        </label>
        <input
          type="date"
          id="startDate"
          value={formData.startDate}
          onChange={(e) =>
            setFormData({ ...formData, startDate: e.target.value })
          }
          className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
          required
        />
      </div>
      {ongoingHabit && (
        <div>
          <label
            htmlFor="status"
            className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
          >
            Status
          </label>
          <select
            id="status"
            value={formData.status}
            onChange={(e) =>
              setFormData({
                ...formData,
                status: e.target.value as "ACTIVE" | "COMPLETED" | "PAUSED",
              })
            }
            className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
          >
            <option value="ACTIVE">Active</option>
            <option value="COMPLETED">Completed</option>
            <option value="PAUSED">Paused</option>
          </select>
        </div>
      )}

      <div className="flex justify-end space-x-3 pt-6">
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors"
        >
          Cancel
        </button>
        <button
          type="submit"
          disabled={isLoading}
          className="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? "Saving..." : ongoingHabit ? "Update" : "Create"}
        </button>
      </div>
    </form>
  );
};
