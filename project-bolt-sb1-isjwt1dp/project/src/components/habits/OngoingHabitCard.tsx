import React from "react";
import { Play, Pause, Check, Edit, Trash2 } from "lucide-react";
import type { OngoingHabit } from "../../types";

interface OngoingHabitCardProps {
  ongoingHabit: OngoingHabit;
  onCheckOff: (id: string) => void;
  onEdit: (ongoingHabit: OngoingHabit) => void;
  onDelete: (id: string) => void;
  index?: number;
}
export const OngoingHabitCard: React.FC<OngoingHabitCardProps> = ({
  ongoingHabit,
  onCheckOff,
  onEdit,
  onDelete,
  index,
}) => {
  const progressPercentage = Math.min(
    (ongoingHabit.counter / ongoingHabit.target) * 100,
    100
  );

  const getStatusColor = (status: string) => {
    switch (status) {
      case "ACTIVE":
        return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-300";
      case "COMPLETED":
        return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300";
      case "PAUSED":
        return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-300";
      default:
        return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-300";
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "ACTIVE":
        return <Play className="h-4 w-4" />;
      case "COMPLETED":
        return <Check className="h-4 w-4" />;
      case "PAUSED":
        return <Pause className="h-4 w-4" />;
      default:
        return null;
    }
  };

  return (
    <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between mb-4">
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
            {ongoingHabit.habit?.name || `Ongoing${index! + 1}`}
          </h3>
          <div className="flex items-center space-x-2">
            <span
              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                ongoingHabit.status
              )}`}
            >
              {getStatusIcon(ongoingHabit.status)}
              <span className="ml-1">{ongoingHabit.status}</span>
            </span>
          </div>
        </div>
        <div className="flex space-x-2">
          <button
            onClick={() => onEdit(ongoingHabit)}
            className="p-2 text-gray-400 hover:text-amber-600 hover:bg-amber-50 dark:hover:bg-amber-900/20 rounded-lg transition-colors"
          >
            <Edit className="h-4 w-4" />
          </button>
          <button
            onClick={() => onDelete(ongoingHabit.ongoingHabitId)}
            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      </div>

      <div className="space-y-3">
        <div className="flex justify-between text-sm">
          <span className="text-gray-600 dark:text-gray-400">Progress</span>
          <span className="font-medium text-gray-900 dark:text-white">
            {ongoingHabit.counter} / {ongoingHabit.target}
          </span>
        </div>
        <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
          <div
            className="bg-blue-600 h-2 rounded-full transition-all duration-300"
            style={{ width: `${progressPercentage}%` }}
          />
        </div>
        <div className="flex justify-between items-center">
          <span className="text-sm font-medium text-blue-600 dark:text-blue-400">
            {progressPercentage.toFixed(1)}%
          </span>
          {(ongoingHabit.status === "STARTED" ||
            ongoingHabit.status === "IN_PROGRESS") &&
            ongoingHabit.counter < ongoingHabit.target && (
              <button
                onClick={() => {
                  console.log(
                    "Checking off habit with ID:",
                    ongoingHabit.ongoingHabitId
                  );
                  onCheckOff(ongoingHabit.ongoingHabitId);
                }}
                className="px-3 py-1 text-sm font-medium text-white bg-green-600 hover:bg-green-700 rounded-lg transition-colors"
              >
                Check Off
              </button>
            )}
        </div>
      </div>
    </div>
  );
};
