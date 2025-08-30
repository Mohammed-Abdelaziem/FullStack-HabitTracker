import React, { useState } from "react";
import type { Habit, CreateHabitRequest } from "../../types";

interface HabitFormProps {
  habit?: Habit;
  onSubmit: (data: CreateHabitRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
}

export const HabitForm: React.FC<HabitFormProps> = ({
  habit,
  onSubmit,
  onCancel,
  isLoading,
}) => {
  const [formData, setFormData] = useState({
    name: habit?.name || "",
    frequency: habit?.frequency || ("DAILY" as const),
    target: habit?.target || 1,
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div>
        <label
          htmlFor="name"
          className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
        >
          Habit Name
        </label>
        <input
          type="text"
          id="name"
          value={formData.name}
          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
          placeholder="Enter habit name"
          required
        />
      </div>
      <div>
        <label
          htmlFor="frequency"
          className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
        >
          Frequency
        </label>
        <select
          id="frequency"
          value={formData.frequency}
          onChange={(e) =>
            setFormData({
              ...formData,
              frequency: e.target.value as "DAILY" | "WEEKLY" | "MONTHLY",
            })
          }
          className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
        >
          <option value="DAILY">Daily</option>
          <option value="WEEKLY">Weekly</option>
          <option value="MONTHLY">Monthly</option>
        </select>
      </div>

      <div>
        <label
          htmlFor="target"
          className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
        >
          Target
        </label>
        <input
          type="number"
          id="target"
          value={formData.target}
          onChange={(e) =>
            setFormData({ ...formData, target: parseInt(e.target.value) })
          }
          className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
          placeholder="Enter target count"
          min="1"
          required
        />
      </div>

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
          {isLoading ? "Saving..." : habit ? "Update Habit" : "Create Habit"}
        </button>
      </div>
    </form>
  );
};
